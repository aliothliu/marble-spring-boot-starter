package io.github.aliothliu.rbac.application;

import io.github.aliothliu.rbac.application.representation.RoleDetailRepresentation;
import io.github.aliothliu.rbac.application.representation.RoleRepresentation;
import io.github.aliothliu.rbac.domain.menu.MenuId;
import io.github.aliothliu.rbac.domain.page.PageId;
import io.github.aliothliu.rbac.domain.role.RoleCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface RbacRoleService {

    Page<RoleRepresentation> paging(String nameLike, Pageable pageable);

    Optional<RoleDetailRepresentation> getOne(RoleCode code);

    RoleCode newRole(RoleCode id, String name, String description);

    RoleCode changeRole(RoleCode id, String name, String description);

    boolean hasMenu(RoleCode roleCode, MenuId menuId);

    boolean hasPage(RoleCode roleCode, PageId pageId);

    boolean hasElement(RoleCode roleCode, String element);

    void grant(RoleCode roleCode, Set<MenuId> menus);

    void grant(RoleCode roleCode, PageId pageId, Set<String> elements);

    void grant(RoleCode roleCode, Map<PageId, Set<String>> params);

    Set<MenuId> loadMenus(RoleCode roleCode);

    Set<PageId> loadPages(RoleCode roleCode);

    Set<String> loadElements(RoleCode roleCode);
}