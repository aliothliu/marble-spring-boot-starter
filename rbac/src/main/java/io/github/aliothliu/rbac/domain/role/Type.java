package io.github.aliothliu.rbac.domain.role;

/**
 * @author Alioth Liu
 **/
public enum Type {
    // 超级管理员标识
    Super("超级"),

    // 标准
    Standard("标准");

    private final String description;

    Type(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
