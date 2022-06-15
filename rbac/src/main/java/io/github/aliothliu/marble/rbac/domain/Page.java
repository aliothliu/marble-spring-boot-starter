package io.github.aliothliu.marble.rbac.domain;

import lombok.Data;
import lombok.NonNull;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Alioth Liu
 **/
@Data
public class Page {

    private final PageId pageId = PageId.uuid();

    @NonNull
    private String name;

    @NonNull
    private Path path;

    private Set<Element> elements = new HashSet<>();

    protected Page() {
    }

    public void changeName(String name) {
        Assert.notNull(name, "页面名称不能为空");
        this.name = name;
    }

    public void changePath(Path path) {
        Assert.notNull(path, "页面路径不能为空");
        this.path = path;
    }

    public void changeElements(Set<Element> elements) {
        Assert.notNull(elements, "页面元素不能为空");
        this.elements = elements;
    }

    public String path() {
        return this.path.getPath();
    }
}
