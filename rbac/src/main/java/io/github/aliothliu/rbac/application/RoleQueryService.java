package io.github.aliothliu.rbac.application;

import io.github.aliothliu.rbac.application.conversion.RoleConversion;
import io.github.aliothliu.rbac.application.representation.RoleDetailRepresentation;
import io.github.aliothliu.rbac.application.representation.RoleRepresentation;
import io.github.aliothliu.rbac.domain.menu.Menu;
import io.github.aliothliu.rbac.domain.menu.MenuId;
import io.github.aliothliu.rbac.domain.page.Element;
import io.github.aliothliu.rbac.domain.page.PageId;
import io.github.aliothliu.rbac.domain.page.PageRepository;
import io.github.aliothliu.rbac.domain.role.Role;
import io.github.aliothliu.rbac.domain.role.RoleCode;
import io.github.aliothliu.rbac.domain.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.casbin.jcasbin.main.Enforcer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Alioth Liu
 **/
@Service
@RequiredArgsConstructor
@Validated
public class RoleQueryService {

    private final RoleRepository repository;

    private final PageRepository pageRepository;

    private final Enforcer enforcer;

    public Page<RoleRepresentation> paging(String nameLike, Pageable pageable) {
        return this.repository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            if (Objects.isNull(nameLike)) {
                return criteriaQuery.getRestriction();
            }
            return criteriaQuery
                    .where(criteriaBuilder.like(root.get(Role.Fields.name), nameLike))
                    .getRestriction();
        }, pageable).map(this::from);
    }

    public Optional<RoleDetailRepresentation> getOne(RoleCode code) {
        return this.repository.findByCode(code).map(this::detailFrom);
    }

    public Set<MenuId> loadMenus(RoleCode code) {
        this.enforcer.loadPolicy();
        return this.enforcer
                .getFilteredPolicy(0, code.getCode(), null, Menu.class.getSimpleName())
                .stream()
                .map(rule -> rule.get(1))
                .map(MenuId::new)
                .collect(Collectors.toSet());
    }

    public Set<PageId> loadPages(RoleCode code) {
        this.enforcer.loadPolicy();
        return this.enforcer
                .getFilteredPolicy(0, code.getCode(), null, Page.class.getSimpleName())
                .stream()
                .map(rule -> rule.get(1))
                .map(PageId::new)
                .collect(Collectors.toSet());
    }

    public Set<String> loadElements(RoleCode code) {
        this.enforcer.loadPolicy();
        return this.enforcer
                .getFilteredPolicy(0, code.getCode(), null, Element.class.getSimpleName())
                .stream()
                .map(rule -> rule.get(1))
                .collect(Collectors.toSet());
    }

    private RoleRepresentation from(Role role) {
        RoleRepresentation representation = RoleConversion.INSTANCE.from(role);
        this.setRepresentation(representation, role);
        return representation;
    }

    private void setRepresentation(RoleRepresentation representation, Role role) {
        representation.setCountOfUser(this.enforcer.getUsersForRole(role.getCode().getCode()).size());
        representation.setCountOfPermission(this.enforcer.getFilteredPolicy(0, role.getCode().getCode()).size());
    }

    private RoleDetailRepresentation detailFrom(Role role) {
        RoleDetailRepresentation representation = RoleConversion.INSTANCE.detailFrom(role);
        this.setRepresentation(representation, role);
        representation.setMenus(this.loadMenus(role.getCode()).stream().map(MenuId::getId).collect(Collectors.toSet()));

        final Set<String> elementIdList = this.loadElements(role.getCode());
        representation.setPages(
                this.pageRepository.findAllById(this.loadPages(role.getCode()))
                        .stream()
                        .map(page -> {
                            RoleDetailRepresentation.Page pageRepresentation = new RoleDetailRepresentation.Page(page.getPageId().getId());
                            pageRepresentation.setElements(
                                    page.getElements()
                                            .stream()
                                            .filter(element -> elementIdList.contains(element.getId()))
                                            .map(Element::getName)
                                            .collect(Collectors.toSet())
                            );

                            return pageRepresentation;
                        }).collect(Collectors.toSet()));
        return representation;
    }
}
