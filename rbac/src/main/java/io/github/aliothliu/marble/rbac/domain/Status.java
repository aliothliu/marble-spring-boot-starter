package io.github.aliothliu.marble.rbac.domain;

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