package io.github.aliothliu.marble.rbac.domain;

import io.github.aliothliu.marble.rbac.MarbleRbacRegistry;
import lombok.Data;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.UUID;

@Data
public class PageId implements Serializable {

    private String id;

    public PageId(String id) {
        Assert.notNull(id, "页面ID不能为空");
        this.id = id;
    }

    public static PageId uuid() {
        return new PageId(UUID.randomUUID().toString());
    }

    public void failFastValidate() {
        if (!MarbleRbacRegistry.pageRepository().existsById(this)) {
            throw new IllegalArgumentException("操作失败：未找到符合条件的页面");
        }
    }
}
