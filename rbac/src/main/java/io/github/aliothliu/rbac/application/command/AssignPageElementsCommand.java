package io.github.aliothliu.rbac.application.command;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Data
@Schema(name = "角色分配页面参数对象")
public class AssignPageElementsCommand {

    @Schema(name = "页面ID")
    @NotBlank(message = "页面ID不能为空")
    private String pageId;

    @Schema(name = "页面元素ID列表")
    @NotEmpty(message = "页面元素不能为空")
    private Set<String> elements;
}