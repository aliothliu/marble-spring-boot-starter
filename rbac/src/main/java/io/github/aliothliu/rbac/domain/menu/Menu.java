package io.github.aliothliu.rbac.domain.menu;

import io.github.aliothliu.rbac.RbacRegistry;
import io.github.aliothliu.rbac.domain.Sort;
import io.github.aliothliu.rbac.domain.page.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Builder
@Getter
@Entity
@Table(name = "#{rbacProperties.jpa.menuTableName}")
@FieldNameConstants
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Menu implements Comparable<Menu> {

    @EmbeddedId
    @NonNull
    private MenuId menuId;

    @Column(name = "name", length = 128, nullable = false)
    @NonNull
    private String name;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "page_id", length = 40))
    })
    private PageId pageId;

    @Column(name = "icon")
    private String icon;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "sort", column = @Column(name = "sort"))
    })
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
        return pageRepository().findById(this.pageId).map(Page::getPath).map(Path::path).orElse(null);
    }

    public PathTarget getPathTarget() {
        if (Objects.isNull(pageId)) {
            return null;
        }
        return pageRepository().findById(this.pageId).map(Page::getPath).map(Path::target).orElse(null);
    }

    public Optional<MenuId> getParentId() {
        return this.pathRepository().findByDescendantAndDistance(this.menuId, 1).map(MenuPath::getAncestor);
    }

    public List<MenuPath> paths(MenuId parentId) {
        List<MenuPath> paths = this.rootPaths(this.menuId, parentId);
        paths.add(this.selfPath(this.menuId));

        return paths;
    }

    private List<MenuPath> rootPaths(MenuId current, MenuId parentId) {
        if (Objects.isNull(parentId)) {
            return new ArrayList<>();
        }
        MenuPathRepository repository = RbacRegistry.menuPathRepository();
        return repository.findByDescendant(parentId).stream().map(mt -> {
            MenuPath path = new MenuPath(mt.getAncestor(), current, mt.getDistance());
            path.increaseDistance();
            return path;
        }).collect(Collectors.toList());
    }

    private MenuPath selfPath(MenuId id) {
        return new MenuPath(id, id, 0);
    }

    public void moveTo(int offset) {

    }

    public void moveTo(MenuId parentId) {
        Objects.requireNonNull(parentId);
        if (this.menuId.equals(parentId)) {
            return;
        }
        MenuId storedParentId = this.pathRepository().findByDescendantAndDistance(this.menuId, 1).map(MenuPath::getAncestor).orElse(null);
        this.moveDescendants(this.menuId, storedParentId);
        this.moveSelf(this.menuId, parentId);
    }

    public void moveDescendantsTo(MenuId parentId) {
        if (Objects.isNull(parentId)) {
            parentId = this.menuId;
        }
        MenuPath path = this.pathRepository().findByAncestorAndDescendant(parentId, this.menuId).orElse(null);
        if (Objects.nonNull(path) && path.getDistance() != 0) {
            // 如果移动的目标是其子类，需要先把子类移动到本类的位置
            MenuId storedParentId = this.pathRepository().findByDescendantAndDistance(this.menuId, 1).map(MenuPath::getAncestor).orElse(null);
            this.moveSelf(parentId, storedParentId);
            this.moveDescendants(parentId, parentId);
        }

        this.moveSelf(this.menuId, parentId);
        this.moveDescendants(this.menuId, parentId);
    }

    public List<Menu> descendants() {
        List<MenuPath> paths = this.pathRepository().findByAncestor(this.menuId);

        return this.repository().findAll((root, criteriaQuery, criteriaBuilder) ->
                criteriaQuery.where(
                        criteriaBuilder
                                .in(root.get(Fields.menuId))
                                .value(paths.stream().map(MenuPath::getDescendant).collect(Collectors.toSet())))
                        .getRestriction());
    }

    private void moveSelf(MenuId current, MenuId parentId) {
        this.pathRepository().deleteByDescendant(current);

        List<MenuPath> paths = this.rootPaths(current, parentId);
        paths.add(this.selfPath(current));

        this.pathRepository().saveAll(paths);
    }

    private void moveDescendants(MenuId current, MenuId parentId) {
        List<MenuPath> paths = this.pathRepository().findByAncestorAndDistance(current, 1);
        for (MenuPath path : paths) {
            this.moveSelf(path.getDescendant(), parentId);
            this.moveDescendants(path.getDescendant(), parentId);
        }
    }

    private MenuRepository repository() {
        return RbacRegistry.menuRepository();
    }

    private MenuPathRepository pathRepository() {
        return RbacRegistry.menuPathRepository();
    }

    private PageRepository pageRepository() {
        return RbacRegistry.pageRepository();
    }

    @Override
    public int compareTo(Menu o) {
        return this.sort.compareTo(o.getSort());
    }
}
