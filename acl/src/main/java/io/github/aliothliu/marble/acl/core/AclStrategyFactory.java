package io.github.aliothliu.marble.acl.core;

public interface AclStrategyFactory {

    <T extends AclStrategy<?>> T getStrategy(Class<T> clazz);
}
