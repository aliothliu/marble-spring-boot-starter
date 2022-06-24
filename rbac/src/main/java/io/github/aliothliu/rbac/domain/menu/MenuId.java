package io.github.aliothliu.rbac.domain.menu;

import io.github.aliothliu.rbac.RbacRegistry;
import io.github.aliothliu.rbac.domain.Identity;
import org.springframework.lang.Nullable;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class MenuId implements Serializable, Identity {

    private String id;

    protected MenuId() {
    }

    public MenuId(String id) {
        this.id = id;
    }

    public static MenuId uuid() {
        return new MenuId(UUID.randomUUID().toString());
    }

    @Nullable
    public static MenuId nullable(String id) {
        if (Objects.isNull(id)) {
            return null;
        }
        return new MenuId(id);
    }

    public void failFastValidate() {
        if (!this.repository().existsById(this)) {
            throw new IllegalArgumentException("操作失败：未找到符合条件的菜单数据");
        }
    }

    private MenuRepository repository() {
        return RbacRegistry.menuRepository();
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Class<?> target() {
        return Menu.class;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MenuId)) {
            return false;
        }
        MenuId menuId = (MenuId) o;
        return Objects.equals(id, menuId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
