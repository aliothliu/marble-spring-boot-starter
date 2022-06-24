package io.github.aliothliu.marble.acl.jpa.annotation;

import java.lang.annotation.*;

/**
 * Disable acl on query method
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface NoAcl {

}
