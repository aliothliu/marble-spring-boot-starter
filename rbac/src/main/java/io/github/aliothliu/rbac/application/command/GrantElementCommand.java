package io.github.aliothliu.rbac.application.command;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Schema(name = "角色分配页面参数对象")
public class GrantElementCommand {

    @Schema(name = "页面元素ID列表")
    @NotEmpty(message = "页面元素不能为空")
    private List<String> elements;
}