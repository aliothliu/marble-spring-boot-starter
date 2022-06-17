package io.github.aliothliu.rbac.application.representation;

import lombok.Data;

@Data
public class ApiRepresentation {

    private String method;

    private String path;
}
