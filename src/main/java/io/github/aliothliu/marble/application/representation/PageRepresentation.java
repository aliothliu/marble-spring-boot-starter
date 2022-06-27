package io.github.aliothliu.marble.application.representation;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.aliothliu.marble.domain.page.PathTarget;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
public class PageRepresentation {

    private String id;

    private String name;

    private String path;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PathTarget target;

    private List<Element> elements = new ArrayList<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Element {

        private String id;

        private String name;

        private String readableName;

        private String method;

        private String url;
    }
}
