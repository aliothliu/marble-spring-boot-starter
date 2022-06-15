package io.github.aliothliu.marble.rbac.domain;

import io.github.aliothliu.marble.rbac.MarbleRbacRegistry;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

@Getter
public class RoleCode implements Serializable {

    private final String code;

    public RoleCode(String code) {
        this.code = code;
    }

    public void failFastValidate() {
        if (!MarbleRbacRegistry.roleRepository().existsByCode(this)) {
            throw new IllegalArgumentException("操作失败：未找到符合条件的角色");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RoleCode)) {
            return false;
        }
        RoleCode roleCode = (RoleCode) o;
        return Objects.equals(code, roleCode.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
