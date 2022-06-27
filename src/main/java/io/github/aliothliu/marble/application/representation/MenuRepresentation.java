package io.github.aliothliu.marble.application.representation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.aliothliu.marble.domain.page.PathTarget;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MenuRepresentation {

    private String id;

    private String name;

    private String pageId;

    private String path;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String parentId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PathTarget target;

    private String icon;

    private List<MenuRepresentation> children = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Element> elements = new ArrayList<>();

    @Data
    @AllArgsConstructor
    public static class Element {

        @JsonIgnore
        private String id;

        private String name;

        private String readableName;
    }
}
