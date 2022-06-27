package io.github.aliothliu.marble.application;

import io.github.aliothliu.marble.application.representation.MenuRepresentation;
import io.github.aliothliu.marble.domain.menu.MenuId;
import io.github.aliothliu.marble.domain.page.PageId;

import java.util.List;
import java.util.Optional;

public interface RbacMenuService {

    List<MenuRepresentation> roots(String nameLike);

    List<MenuRepresentation> forest();

    Optional<MenuRepresentation> getOne(MenuId id);

    MenuId append(String name, PageId pageId, String icon, MenuId parentId);

    void moveTo(MenuId menu, int offset);

    void update(MenuId menu, String name, PageId pageId, String icon, MenuId parentId);

    void remove(MenuId menu);
}
