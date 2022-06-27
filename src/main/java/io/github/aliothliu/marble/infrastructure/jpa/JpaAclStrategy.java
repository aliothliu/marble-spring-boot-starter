package io.github.aliothliu.marble.infrastructure.jpa;

import io.github.aliothliu.marble.infrastructure.acl.AclStrategy;
import org.springframework.data.jpa.domain.Specification;

public interface JpaAclStrategy<T> extends AclStrategy<Specification<T>> {
}
