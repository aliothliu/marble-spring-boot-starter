package io.github.aliothliu.marble.acl;

import io.github.aliothliu.marble.infrastructure.jpa.JpaAclStrategy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component(value = "CreatorJpaAclStrategy")
public class CreatorJpaAclStrategy<T> implements JpaAclStrategy<T> {

    @Override
    public Optional<Specification<T>> criteria() {
        return Optional.of((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("createdBy"), "admin"));
    }
}
