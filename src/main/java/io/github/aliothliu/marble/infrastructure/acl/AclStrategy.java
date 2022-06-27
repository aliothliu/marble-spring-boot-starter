package io.github.aliothliu.marble.infrastructure.acl;

import java.util.Optional;

@FunctionalInterface
public interface AclStrategy<T> {

    Optional<T> criteria();
}