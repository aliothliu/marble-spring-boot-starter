package io.github.aliothliu.marble.infrastructure.acl;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Acl {

    Class<? extends AclStrategy> strategy();
}
