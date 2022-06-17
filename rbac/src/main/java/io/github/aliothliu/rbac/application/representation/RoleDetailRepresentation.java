package io.github.aliothliu.rbac.application.representation;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class RoleDetailRepresentation extends RoleRepresentation {

    Set<String> menus = new HashSet<>();

    Set<Page> pages = new HashSet<>();

    @Data
    @AllArgsConstructor
    @RequiredArgsConstructor
    public static class Page {

        @NonNull
        private String id;

        private Set<String> elements;
    }
}
