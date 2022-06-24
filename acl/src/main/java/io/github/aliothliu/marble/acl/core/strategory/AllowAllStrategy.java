package io.github.aliothliu.marble.acl.core.strategory;

import io.github.aliothliu.marble.acl.core.AclStrategy;

import java.util.Optional;

public class AllowAllStrategy<T> implements AclStrategy<T> {

    @Override
    public Optional<T> criteria() {
        return Optional.empty();
    }
}
