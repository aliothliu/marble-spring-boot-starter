package io.github.aliothliu.marble.rbac.domain;

public interface RoleRepository {

    boolean existsByCode(RoleCode code);
}
