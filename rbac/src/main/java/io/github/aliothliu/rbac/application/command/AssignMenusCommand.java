package io.github.aliothliu.rbac.application.command;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Schema(name = "角色分配菜单参数对象")
public class AssignMenusCommand {
    @Schema(name = "菜单列表")
    @NotEmpty(message = "菜单不能为空")
    private List<String> menus;
}
