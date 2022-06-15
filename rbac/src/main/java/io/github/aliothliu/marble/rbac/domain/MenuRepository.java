package io.github.aliothliu.marble.rbac.domain;

import java.util.List;
import java.util.Optional;

public interface MenuRepository {

    void saveAll(List<MenuTree> trees);

    boolean exists(MenuId menuId);

    Optional<MenuId> parent(MenuId current);

    List<MenuTree> trees(MenuId current);

    int distance(MenuId current, MenuId parent);

    List<Menu> descendants(MenuId current);

    List<Menu> children(MenuId current);

    void removeTree(MenuId current);
}
