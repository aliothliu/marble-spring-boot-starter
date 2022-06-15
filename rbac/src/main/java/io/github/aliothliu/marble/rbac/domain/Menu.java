package io.github.aliothliu.marble.rbac.domain;

import io.github.aliothliu.marble.rbac.MarbleRbacRegistry;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Alioth Liu
 **/
@Builder
@Getter
@FieldNameConstants
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Menu implements Comparable<Menu> {

    private final MenuId menuId = MenuId.uuid();

    @NonNull
    private String name;

    private PageId pageId;

    private String icon;

    @Setter
    private Sort sort;

    public void changeName(String name) {
        this.name = name;
    }

    public void changeIcon(String icon) {
        this.icon = icon;
    }

    public void changePageId(PageId pageId) {
        if (Objects.nonNull(pageId)) {
            pageId.failFastValidate();
            this.pageId = pageId;
        }
    }

    public String getPagePath() {
        if (Objects.isNull(pageId)) {
            return null;
        }
        return pageRepository().ofId(this.pageId).map(Page::getPath).map(Path::getPath).orElse(null);
    }

    public PathTarget getPathTarget() {
        if (Objects.isNull(pageId)) {
            return PathTarget._blank;
        }
        return pageRepository().ofId(this.pageId).map(Page::getPath).map(Path::getTarget).orElse(PathTarget._blank);
    }

    public Optional<MenuId> getParentId() {
        return this.repository().parent(this.menuId);
    }

    private List<MenuTree> newTree(MenuId current, MenuId parentId) {
        if (Objects.isNull(parentId)) {
            return new ArrayList<>();
        }
        List<MenuTree> dictionaries = this.repository().trees(parentId).stream().map(mt -> {
            MenuTree path = new MenuTree(mt.getAncestor(), current, mt.getDistance());
            path.increaseDistance();
            return path;
        }).collect(Collectors.toList());

        dictionaries.add(this.self(current));

        return dictionaries;
    }

    private MenuTree self(MenuId id) {
        return new MenuTree(id, id, 0);
    }

    public void moveTo(MenuId parentId) {
        Objects.requireNonNull(parentId);
        if (this.menuId.equals(parentId)) {
            return;
        }
        MenuId storedParentId = this.repository().parent(this.menuId).orElse(null);
        this.moveDescendants(this.menuId, storedParentId);
        this.moveSelf(this.menuId, parentId);
    }

    public void moveDescendantsTo(MenuId parentId) {
        if (Objects.isNull(parentId)) {
            parentId = this.menuId;
        }
        int distance = this.repository().distance(this.menuId, parentId);
        if (distance != 0) {
            // 如果移动的目标是其子类，需要先把子类移动到本类的位置
            MenuId storedParentId = this.repository().parent(this.menuId).orElse(null);
            this.moveSelf(parentId, storedParentId);
            this.moveDescendants(parentId, parentId);
        }

        this.moveSelf(this.menuId, parentId);
        this.moveDescendants(this.menuId, parentId);
    }

    public List<Menu> descendants() {
        return this.repository().descendants(this.menuId);
    }

    private void moveSelf(MenuId current, MenuId parentId) {
        this.repository().removeTree(current);

        List<MenuTree> trees = this.newTree(current, parentId);

        this.repository().saveAll(trees);
    }

    private void moveDescendants(MenuId current, MenuId parentId) {
        List<Menu> children = this.repository().children(current);
        for (Menu child : children) {
            this.moveSelf(child.getMenuId(), parentId);
            this.moveDescendants(child.getMenuId(), parentId);
        }
    }

    private MenuRepository repository() {
        return MarbleRbacRegistry.menuRepository();
    }

    private PageRepository pageRepository() {
        return MarbleRbacRegistry.pageRepository();
    }

    @Override
    public int compareTo(Menu o) {
        return this.sort.compareTo(o.getSort());
    }
}