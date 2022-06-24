package io.github.aliothliu.rbac.domain.page;

import io.github.aliothliu.rbac.RbacRegistry;
import io.github.aliothliu.rbac.domain.Identity;
import lombok.EqualsAndHashCode;
import org.springframework.util.Assert;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
@EqualsAndHashCode
public class PageId implements Serializable, Identity {

    private String id;

    protected PageId() {
    }

    public PageId(String id) {
        Assert.notNull(id, "页面ID不能为空");
        this.id = id;
    }

    public static PageId uuid() {
        return new PageId(UUID.randomUUID().toString());
    }

    public void failFastValidate() {
        if (!RbacRegistry.pageRepository().existsById(this)) {
            throw new IllegalArgumentException("操作失败：未找到符合条件的页面");
        }
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
        return Page.class;
    }
}
