package io.github.aliothliu.marble.domain.role;

/**
 * @author Alioth Liu
 **/
public enum Status {
    // 启用
    ENABLE("启用"),
    // 禁用
    DISABLE("禁用");

    private final String description;

    Status(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}