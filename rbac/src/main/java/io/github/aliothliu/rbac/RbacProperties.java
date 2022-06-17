package io.github.aliothliu.rbac;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "rbacProperties")
@ConfigurationProperties(prefix = "marble.rbac")
@Data
public class RbacProperties {

    private Web web;

    private Jpa jpa;

    private String administrator = "administrator";

    @Data
    public static class Web {
        private String prefix = "api";
    }

    @Data
    public static class Jpa {
        private String ruleTableName = "rbac";
        private String roleTableName = "rbac";
        private String menuTableName = "rbac";
        private String menuPathTableName = "rbac";
        private String pageTableName = "rbac";
        private String pageElementTableName = "rbac";
        private String pageElementRefTableName = "rbac";
    }
}
