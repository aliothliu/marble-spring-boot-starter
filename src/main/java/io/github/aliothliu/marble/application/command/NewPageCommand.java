package io.github.aliothliu.marble.application.command;

import io.github.aliothliu.marble.domain.page.PathTarget;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Data
@Schema(name = "新建页面请求参数")
public class NewPageCommand {

    @Schema(name = "页面名称")
    @NotBlank(message = "页面名称不能为空")
    private String name;

    @Schema(name = "页面路径")
    @NotBlank(message = "页面路径不能为空")
    private String path;

    @Schema(name = "页面链接打开方式，默认: _self")
    private PathTarget target = PathTarget._self;

    @Schema(name = "页面元素")
    @Valid
    private List<Element> elements = new ArrayList<>();

    @Data
    @Schema(name = "页面元素")
    public static class Element {

        @Schema(name = "页面元素标识，建议英文如:btn-create，在页面内唯一")
        @NotBlank(message = "元素标识不能为空")
        private String name;

        @Schema(name = "页面元素可读名称")
        @NotBlank(message = "元素可读名称不能为空")
        private String readableName;

        @Schema(name = "页面名称")
        private String method;

        @Schema(name = "页面名称")
        private String uri;
    }
}
