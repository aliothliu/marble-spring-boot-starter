package io.github.aliothliu.marble.application.representation;

import lombok.Data;

@Data
public class RoleRepresentation {

    private String id;

    private String code;

    private String name;

    private String type;

    private String typeDesc;

    private String status;

    private String statusDesc;

    private int countOfUser;

    private int countOfPermission;

    private String description;
}
