package io.github.aliothliu.rbac.application.command;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@Schema(name = "新建角色参数对象")
public class NewRoleCommand {

    @NotBlank(message = "角色编码不能为空")
    @Length(max = 128, message = "角色编码长度不能超过{max}")
    @Schema(name = "角色编码")
    private String code;

    @NotBlank(message = "角色名称不能为空")
    @Length(max = 255, message = "角色名称长度不能超过{max}")
    @Schema(name = "角色名称")
    private String name;

    @Schema(name = "描述")
    @Length(max = 255, message = "角色名称长度不能超过{max}")
    private String description;
}
