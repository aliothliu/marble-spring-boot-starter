package io.github.aliothliu.marble.acl.jpa;

import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

class JpaAllowAllStrategy<T> implements JpaAclStrategy<Specification<T>> {

    @Override
    public Optional<Specification<Specification<T>>> criteria() {
        return Optional.empty();
    }
}
