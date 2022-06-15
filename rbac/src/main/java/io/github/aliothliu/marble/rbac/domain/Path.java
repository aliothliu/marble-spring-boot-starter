package io.github.aliothliu.marble.rbac.domain;

import lombok.Data;

import java.util.Objects;

@Data
public class Path {

    private String path;

    private PathTarget target;

    protected Path() {
    }

    public Path(String path) {
        if (Objects.isNull(path)) {
            throw new IllegalArgumentException("页面路径不能为空");
        }
        this.setPath(path);
    }

    public Path(String path, PathTarget target) {
        if (Objects.isNull(path)) {
            throw new IllegalArgumentException("页面路径不能为空");
        }
        this.setPath(path);
        this.target = target;
    }
}
