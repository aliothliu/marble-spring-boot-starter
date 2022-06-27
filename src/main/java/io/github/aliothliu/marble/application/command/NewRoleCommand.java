package io.github.aliothliu.marble.application.command;

import io.github.aliothliu.marble.domain.role.Type;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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

    @NotNull(message = "角色类型不能为空")
    @Schema(name = "角色类型")
    private Type type;

    @Schema(name = "描述")
    @Length(max = 255, message = "角色名称长度不能超过{max}")
    private String description;
}
