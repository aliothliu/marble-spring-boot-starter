package io.github.aliothliu.rbac.domain.page;

import io.github.aliothliu.rbac.RbacRegistry;
import org.springframework.util.Assert;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class ElementId implements Serializable {

    private String id;

    protected ElementId() {
    }

    public ElementId(String id) {
        Assert.notNull(id, "元素ID不能为空");
        this.id = id;
    }

    public static ElementId uuid() {
        return new ElementId(UUID.randomUUID().toString());
    }

    public void failFastValidate() {
        if (!RbacRegistry.elementRepository().existsById(this)) {
            throw new IllegalArgumentException("操作失败：未找到符合条件的元素");
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ElementId)) return false;
        ElementId elementId = (ElementId) o;
        return Objects.equals(id, elementId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
