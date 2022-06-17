package io.github.aliothliu.rbac.application.command;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@Schema(name = "修改角色参数对象")
public class ChangeRoleCommand {

    @NotBlank(message = "角色名称不能为空")
    @Length(max = 255, message = "角色名称长度不能超过{max}")
    @Schema(name = "角色名称")
    private String name;

    @Schema(name = "描述")
    @Length(max = 255, message = "角色名称长度不能超过{max}")
    private String description;
}
