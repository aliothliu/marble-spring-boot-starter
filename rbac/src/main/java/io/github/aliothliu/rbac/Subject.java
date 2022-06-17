package io.github.aliothliu.rbac;

import io.github.aliothliu.rbac.application.representation.MenuRepresentation;
import io.github.aliothliu.rbac.domain.menu.MenuId;
import io.github.aliothliu.rbac.domain.page.PageId;
import io.github.aliothliu.rbac.domain.role.RoleCode;

import java.util.List;
import java.util.Set;

/**
 * 可以被授权的人或者事物，主体之间可以存在特定关系，分别是包含、继承、隶属，在系统中可以理解为用户、角色、组等。
 */
public interface Subject {

    String identity();

    boolean hasMenu(MenuId menuId);

    boolean hasPageElement(PageId pageId, String elementId);

    void grant(RoleCode roleCode);

    void grantAll(List<RoleCode> roleCode);

    void revoke(RoleCode roleCode);

    void revokeAll();

    Set<MenuId> loadMenus();

    List<MenuRepresentation> loadMenuForest();

    Set<RoleCode> loadRoles();

    Set<String> loadPageElements();
}
