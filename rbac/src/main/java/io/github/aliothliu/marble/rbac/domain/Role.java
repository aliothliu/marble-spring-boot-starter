package io.github.aliothliu.marble.rbac.domain;

import io.github.aliothliu.marble.rbac.AssertionConcern;
import io.github.aliothliu.marble.rbac.MarbleRbacRegistry;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;

@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
@Getter
@FieldNameConstants
public class Role extends AssertionConcern implements Serializable {

    @NonNull
    private RoleCode code;

    @NonNull
    private String name;

    @Builder.Default
    private Status status = Status.ENABLE;

    private String description;

    public void validate() {
        this.assertArgumentFalse(MarbleRbacRegistry.roleRepository().existsByCode(this.code), "不允许重复的角色编码[" + code.getCode() + "]");
    }

    public void changeName(String name) {
        this.assertArgumentNotNull(name, "角色名称不能为空");
        this.name = name;
    }

    public void changeDescription(String description) {
        this.description = description;
    }

    public void changeStatus(Status status) {
        this.assertArgumentNotNull(name, "角色状态不能为空");
        this.status = status;
    }
}
