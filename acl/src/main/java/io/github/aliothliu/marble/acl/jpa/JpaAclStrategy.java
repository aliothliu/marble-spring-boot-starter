package io.github.aliothliu.marble.acl.jpa;

import io.github.aliothliu.marble.acl.core.AclStrategy;
import org.springframework.data.jpa.domain.Specification;

public interface JpaAclStrategy<T> extends AclStrategy<Specification<T>> {
}
