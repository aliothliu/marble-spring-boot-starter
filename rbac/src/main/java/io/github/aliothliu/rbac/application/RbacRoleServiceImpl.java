package io.github.aliothliu.rbac.application;

import io.github.aliothliu.rbac.application.representation.RoleDetailRepresentation;
import io.github.aliothliu.rbac.application.representation.RoleRepresentation;
import io.github.aliothliu.rbac.domain.FailFastValidationHandler;
import io.github.aliothliu.rbac.domain.menu.Menu;
import io.github.aliothliu.rbac.domain.menu.MenuId;
import io.github.aliothliu.rbac.domain.menu.MenuRepository;
import io.github.aliothliu.rbac.domain.page.Element;
import io.github.aliothliu.rbac.domain.page.PageElementsValidator;
import io.github.aliothliu.rbac.domain.page.PageId;
import io.github.aliothliu.rbac.domain.page.PageRepository;
import io.github.aliothliu.rbac.domain.role.Role;
import io.github.aliothliu.rbac.domain.role.RoleCode;
import io.github.aliothliu.rbac.domain.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.casbin.jcasbin.main.Enforcer;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class RbacRoleServiceImpl implements RbacRoleService {

    private final RoleRepository repository;
    private final MenuRepository menuRepository;
    private final PageRepository pageRepository;
    private final Enforcer enforcer;

    @Override
    public Page<RoleRepresentation> paging(String nameLike, Pageable pageable) {
        return this.repository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            if (Objects.nonNull(nameLike)) {
                return criteriaQuery.getRestriction();
            }
            return criteriaQuery
                    .where(criteriaBuilder.like(root.get(Role.Fields.name), nameLike))
                    .getRestriction();
        }, pageable).map(this::from);
    }

    @Override
    public Optional<RoleDetailRepresentation> getOne(RoleCode code) {
        return this.repository.findByCode(code).map(this::detailFrom);
    }

    @Override
    public RoleCode newRole(RoleCode code, String name, String description) {
        Role role = Role.builder().code(code).name(name).description(description).build();
        this.repository.save(role);
        return role.getCode();
    }

    @Override
    public RoleCode changeRole(RoleCode code, String name, String description) {
        Role role = this.repository.findByCode(code).orElseThrow(() -> new EntityNotFoundException("角色更新失败：未查询到匹配的角色数据"));
        role.changeName(name);
        role.changeDescription(description);
        this.repository.save(role);
        return role.getCode();
    }

    @Override
    public boolean hasMenu(RoleCode code, MenuId menuId) {
        this.enforcer.loadPolicy();
        return this.enforcer.hasPolicy(Arrays.asList(code.getCode(), menuId.getId(), Menu.class.getSimpleName()));
    }

    @Override
    public boolean hasPage(RoleCode code, PageId pageId) {
        this.enforcer.loadPolicy();
        return this.enforcer.hasPolicy(Arrays.asList(code.getCode(), pageId.getId(), Page.class.getSimpleName()));
    }

    @Override
    public boolean hasElement(RoleCode code, String element) {
        this.enforcer.loadPolicy();
        return this.enforcer.hasPolicy(Arrays.asList(code.getCode(), element, Element.class.getSimpleName()));
    }

    @Override
    public void grant(RoleCode code, Set<MenuId> menus) {
        code.failFastValidate();
        // 移除现有关联菜单
        this.enforcer.removePolicy(code.getCode());
        this.enforcer.loadPolicy();
        // 授权菜单策略
        List<Menu> storedMenus = this.menuRepository.findAllById(menus);
        List<List<String>> policies = new ArrayList<>(this.convertMenu2Policy(code, storedMenus));
        // 授权页面策略
        List<PageId> pageIds = storedMenus
                .stream()
                .map(Menu::getPageId)
                .filter(pageId -> Objects.nonNull(pageId) && Objects.nonNull(pageId.getId()))
                .collect(Collectors.toList());
        policies.addAll(this.convertPage2Policy(code, pageIds));

        this.enforcer.addPolicies(policies);
    }

    private List<List<String>> convertMenu2Policy(RoleCode code, List<Menu> menus) {
        return menus.stream().map(menu -> Arrays.asList(code.getCode(), menu.getMenuId().getId(), Menu.class.getSimpleName())).collect(Collectors.toList());
    }

    private List<List<String>> convertPage2Policy(RoleCode code, List<PageId> pageIds) {
        return pageIds.stream().map(pageId -> Arrays.asList(code.getCode(), pageId.getId(), Page.class.getSimpleName())).collect(Collectors.toList());
    }

    private List<List<String>> convertElement2Policy(RoleCode code, List<PageId> pageIds) {
        return this.pageRepository.findAllById(pageIds)
                .stream()
                .map(io.github.aliothliu.rbac.domain.page.Page::getElements)
                .flatMap(Set::stream)
                .map(element -> Arrays.asList(code.getCode(), element.getId(), Element.class.getSimpleName()))
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void grant(RoleCode code, PageId pageId, Set<String> elements) {
        code.failFastValidate();
        pageId.failFastValidate();
        new PageElementsValidator(new FailFastValidationHandler(), pageId).validate(elements);
        this.enforcer.loadPolicy();
        if (!this.hasPage(code, pageId)) {
            this.enforcer.addPolicy(Arrays.asList(code.getCode(), pageId.getId(), Page.class.getSimpleName()));
        }
        Set<Element> storedElements = this.pageRepository
                .findById(pageId)
                .map(io.github.aliothliu.rbac.domain.page.Page::getElements)
                .orElse(new HashSet<>());
        // clear all granted elements
        this.enforcer.removePolicies(
                storedElements
                        .stream()
                        .map(element -> Arrays.asList(code.getCode(), element.getId(), Element.class.getSimpleName()))
                        .collect(Collectors.toList())
        );
        this.enforcer.loadPolicy();
        // grant all element
        this.enforcer.addPolicies(elements.stream().map(element -> Arrays.asList(code.getCode(), element, Element.class.getSimpleName())).collect(Collectors.toList()));
    }

    @Override
    public void grant(RoleCode roleId, Map<PageId, Set<String>> params) {
        if (Objects.isNull(params)) {
            return;
        }
        params.forEach((k, v) -> this.grant(roleId, k, v));
    }

    @Override
    public Set<MenuId> loadMenus(RoleCode code) {
        this.enforcer.loadPolicy();
        return this.enforcer
                .getFilteredPolicy(0, code.getCode(), null, Menu.class.getSimpleName())
                .stream()
                .map(rule -> rule.get(1))
                .map(MenuId::new)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<PageId> loadPages(RoleCode code) {
        this.enforcer.loadPolicy();
        return this.enforcer
                .getFilteredPolicy(0, code.getCode(), null, Page.class.getSimpleName())
                .stream()
                .map(rule -> rule.get(1))
                .map(PageId::new)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<String> loadElements(RoleCode code) {
        this.enforcer.loadPolicy();
        return this.enforcer
                .getFilteredPolicy(0, code.getCode(), null, Element.class.getSimpleName())
                .stream()
                .map(rule -> rule.get(1))
                .collect(Collectors.toSet());
    }

    @Deprecated
    protected void handleMenuCreated(MenuId menuId, PageId pageId) {
        this.repository.findByCode(RoleCode.administrator()).ifPresent(role -> {
            // 授权菜单策略
            List<Menu> storedMenus = this.menuRepository.findAllById(Collections.singleton(menuId));
            List<List<String>> policies = new ArrayList<>(this.convertMenu2Policy(role.getCode(), storedMenus));
            // 授权页面策略
            List<PageId> pageIds = storedMenus.stream().map(Menu::getPageId).collect(Collectors.toList());
            policies.addAll(this.convertPage2Policy(role.getCode(), pageIds));
            // 授权元素策略
            policies.addAll(this.convertElement2Policy(role.getCode(), pageIds));

            this.enforcer.addPolicies(policies);
        });
    }

    private RoleRepresentation from(Role role) {
        RoleRepresentation representation = new RoleRepresentation();
        this.setRepresentation(representation, role);
        return representation;
    }

    private void setRepresentation(RoleRepresentation representation, Role role) {
        BeanUtils.copyProperties(role, representation);
        representation.setId(role.getId());
        representation.setCode(role.getCode().getCode());
        representation.setStatus(role.getStatus().name());
        representation.setStatusDesc(role.getStatus().getDescription());
        representation.setCountOfUser(this.enforcer.getUsersForRole(role.getCode().getCode()).size());
        representation.setCountOfPermission(this.enforcer.getFilteredPolicy(0, role.getCode().getCode()).size());
    }

    private RoleDetailRepresentation detailFrom(Role role) {
        RoleDetailRepresentation representation = new RoleDetailRepresentation();
        this.setRepresentation(representation, role);
        representation.setMenus(this.loadMenus(role.getCode()).stream().map(MenuId::getId).collect(Collectors.toSet()));

        Set<String> elementIdList = this.loadElements(role.getCode());
        representation.setPages(
                this.loadPages(role.getCode())
                        .stream()
                        .map(pageId -> this.pageRepository.findById(pageId)
                                .map(page -> {
                                    RoleDetailRepresentation.Page pageRepresentation = new RoleDetailRepresentation.Page(pageId.getId());
                                    pageRepresentation.setElements(
                                            page.getElements()
                                                    .stream()
                                                    .filter(element -> elementIdList.contains(element.getId()))
                                                    .map(Element::getName)
                                                    .collect(Collectors.toSet())
                                    );

                                    return pageRepresentation;
                                }).orElse(null)).collect(Collectors.toSet())
        );

        return representation;
    }
}
