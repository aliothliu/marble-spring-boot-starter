package io.github.aliothliu.marble.application.command;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(name = "新建菜单请求")
public class ChangeMenuCommand {

    @Schema(name = "菜单名称")
    @NotBlank(message = "菜单名称不能为空")
    private String name;

    @Schema(name = "页面ID")
    private String pageId;

    @Schema(name = "菜单图标")
    private String icon;

    @Schema(name = "父级菜单")
    private String parentId;
}
