package io.github.aliothliu.marble.domain.page;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Objects;

@Embeddable
public class Path {

    @Column(name = "path", length = 128)
    private String path;

    @Column(name = "target", length = 128)
    @Enumerated(value = EnumType.STRING)
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public PathTarget getTarget() {
        return this.target;
    }
}
