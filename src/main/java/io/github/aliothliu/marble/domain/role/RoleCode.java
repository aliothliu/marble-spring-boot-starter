package io.github.aliothliu.marble.domain.role;

import io.github.aliothliu.marble.MarbleRegistry;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoleCode implements Serializable {

    private String code;

    public RoleCode(String code) {
        this.code = code;
    }

    public void failFastValidate() {
        if (!MarbleRegistry.roleRepository().existsByCode(this)) {
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
