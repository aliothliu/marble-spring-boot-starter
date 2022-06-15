package io.github.aliothliu.marble.rbac.domain;

import io.github.aliothliu.marble.rbac.MarbleRbacRegistry;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Data
public class MenuId implements Serializable {

    private String id;

    protected MenuId() {
    }

    public MenuId(String id) {
        this.id = id;
    }

    public static MenuId uuid() {
        return new MenuId(UUID.randomUUID().toString());
    }

    public static MenuId nullable(String id) {
        if (Objects.isNull(id)) {
            return null;
        }
        return new MenuId(id);
    }

    public void failFastValidate() {
        if (!this.repository().exists(this)) {
            throw new IllegalArgumentException("操作失败：未找到符合条件的菜单数据");
        }
    }

    private MenuRepository repository() {
        return MarbleRbacRegistry.menuRepository();
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
