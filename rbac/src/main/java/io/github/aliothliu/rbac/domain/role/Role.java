package io.github.aliothliu.rbac.domain.role;

import io.github.aliothliu.rbac.RbacRegistry;
import io.github.aliothliu.rbac.domain.AssertionConcern;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.io.Serializable;

@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
@Entity
@Table(name = "#{rbacProperties.jpa.roleTableName}")
@Getter
@FieldNameConstants
public class Role extends AssertionConcern implements Serializable {

    @Id
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "uuid")
    private String id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "code", column = @Column(name = "code", length = 40))
    })
    @NonNull
    private RoleCode code;

    @NonNull
    private String name;

    @Column(name = "status", length = 32)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.ENABLE;

    private String description;

    public void validate() {
        this.assertArgumentFalse(RbacRegistry.roleRepository().existsByCode(this.code), "不允许重复的角色编码[" + code.getCode() + "]");
    }

    public void changeName(String name) {
        Assert.notNull(name, "角色名称不能为空");
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
