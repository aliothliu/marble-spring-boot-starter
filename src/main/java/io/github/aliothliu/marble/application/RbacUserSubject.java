package io.github.aliothliu.marble.application;

import io.github.aliothliu.marble.MarbleRegistry;
import io.github.aliothliu.marble.Subject;
import io.github.aliothliu.marble.application.representation.MenuRepresentation;
import io.github.aliothliu.marble.domain.menu.Menu;
import io.github.aliothliu.marble.domain.menu.MenuId;
import io.github.aliothliu.marble.domain.page.Element;
import io.github.aliothliu.marble.domain.page.PageId;
import io.github.aliothliu.marble.domain.role.RoleCode;
import org.casbin.jcasbin.main.Enforcer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class RbacUserSubject implements Subject {

    private final String identity;

    public RbacUserSubject(String identity, boolean reloadPolicy) {
        if (reloadPolicy) {
            this.enforcer().loadPolicy();
        }
        this.identity = identity;
    }

    public RbacUserSubject(String identity) {
        this(identity, false);
    }

    @Override
    public String identity() {
        return this.identity;
    }

    @Override
    public boolean hasMenu(MenuId menuId) {
        return this.enforcer()
                .getImplicitPermissionsForUser(this.identity)
                .stream()
                .anyMatch(l -> l.contains(menuId.getId()));
    }

    @Override
    public boolean hasPageElement(PageId pageId, String elementId) {
        return this.enforcer()
                .getImplicitPermissionsForUser(this.identity)
                .stream()
                .anyMatch(l -> l.contains(elementId));
    }

    @Override
    public void grant(RoleCode roleCode) {
        this.enforcer().addRoleForUser(this.identity, roleCode.getCode());
    }

    @Override
    public void grantAll(List<RoleCode> roleCode) {
        if (Objects.isNull(roleCode)) {
            return;
        }
        roleCode.forEach(this::grant);
    }

    @Override
    public void revoke(RoleCode roleCode) {
        this.enforcer().deleteRoleForUser(this.identity, roleCode.getCode());
    }

    @Override
    public void revokeAll() {
        this.enforcer().deleteRolesForUser(this.identity);
    }

    @Override
    public Set<MenuId> loadMenus() {
        return this.enforcer()
                .getImplicitPermissionsForUser(this.identity)
                .stream()
                .filter(policy -> policy.contains(Menu.class.getSimpleName()))
                .map(policy -> policy.get(1))
                .distinct()
                .map(MenuId::new)
                .collect(Collectors.toSet());
    }

    @Override
    public List<MenuRepresentation> loadMenuForest() {
        RbacMenuServiceImpl menuManager = (RbacMenuServiceImpl) this.menuManager();
        Set<MenuId> limit = this.loadMenus();
        Set<String> elements = this.loadPageElements();
        if (limit.isEmpty()) {
            return new ArrayList<>();
        }
        return this.filterElements(menuManager.limitMenus(limit), elements);
    }

    private List<MenuRepresentation> filterElements(List<MenuRepresentation> menus, Set<String> elements) {
        if (Objects.isNull(menus)) {
            return new ArrayList<>();
        }
        return menus.stream().peek(menu -> {
            menu.setElements(menu.getElements().stream().filter(element -> elements.contains(element.getId())).collect(Collectors.toList()));

            this.filterElements(menu.getChildren(), elements);
        }).collect(Collectors.toList());
    }

    @Override
    public Set<RoleCode> loadRoles() {
        return this.enforcer().getRolesForUser(this.identity).stream().map(RoleCode::new).collect(Collectors.toSet());
    }

    @Override
    public Set<String> loadPageElements() {
        return this.enforcer()
                .getImplicitPermissionsForUser(this.identity)
                .stream()
                .filter(policy -> policy.contains(Element.class.getSimpleName()))
                .map(policy -> policy.get(1))
                .collect(Collectors.toSet());
    }

    private Enforcer enforcer() {
        return MarbleRegistry.enforcer();
    }

    private RbacMenuService menuManager() {
        return MarbleRegistry.menuManager();
    }
}
