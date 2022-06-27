package io.github.aliothliu.marble.infrastructure.acl;

public interface AclStrategyFactory {

    <T extends AclStrategy<?>> T getStrategy(Class<T> clazz);
}
