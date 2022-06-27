package io.github.aliothliu.marble.infrastructure.acl.strategory;

import io.github.aliothliu.marble.infrastructure.acl.AclStrategy;

import java.util.Optional;

public class AllowAllStrategy<T> implements AclStrategy<T> {

    @Override
    public Optional<T> criteria() {
        return Optional.empty();
    }
}
