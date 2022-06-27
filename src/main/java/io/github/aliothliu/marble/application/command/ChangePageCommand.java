package io.github.aliothliu.marble.application.command;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Data
@Schema(name = "修改页面请求参数")
public class ChangePageCommand {

    @Schema(name = "页面名称")
    @NotBlank(message = "页面名称不能为空")
    private String name;

    @Schema(name = "页面路径")
    @NotBlank(message = "页面路径不能为空")
    private String path;

    @Schema(name = "页面元素")
    @Valid
    private List<NewPageCommand.Element> elements = new ArrayList<>();
}
