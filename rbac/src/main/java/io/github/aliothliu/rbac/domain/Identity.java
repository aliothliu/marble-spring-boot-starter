package io.github.aliothliu.rbac.domain;

/**
 * @author Alioth Liu
 **/
public interface Identity {
    String getId();

    Class<?> target();
}
