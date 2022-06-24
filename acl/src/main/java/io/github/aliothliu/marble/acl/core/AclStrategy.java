package io.github.aliothliu.marble.acl.core;

import java.util.Optional;

@FunctionalInterface
public interface AclStrategy<T> {

    Optional<T> criteria();
}