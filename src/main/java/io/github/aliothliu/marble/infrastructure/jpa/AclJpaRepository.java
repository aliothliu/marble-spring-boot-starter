package io.github.aliothliu.marble.infrastructure.jpa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.lang.NonNull;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.io.Serializable;

public class AclJpaRepository<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final JpaAclStrategy<T> jpaAclStrategy;

    public AclJpaRepository(JpaEntityInformation<T, ?> entityInformation,
                            EntityManager entityManager,
                            JpaAclStrategy<T> jpaAclStrategy) {
        super(entityInformation, entityManager);
        this.jpaAclStrategy = jpaAclStrategy;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    protected <S extends T> TypedQuery<Long> getCountQuery(Specification<S> spec,
                                                           @NonNull Class<S> domainClass) {
        return super.getCountQuery(((Specification<S>) aclJpaSpec()).and(spec), domainClass);
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    protected <S extends T> TypedQuery<S> getQuery(Specification<S> spec, @NonNull Class<S> domainClass,
                                                   @NonNull Sort sort) {
        return super.getQuery(((Specification<S>) aclJpaSpec()).and(spec), domainClass, sort);
    }

    private Specification<T> aclJpaSpec() {
        return this.jpaAclStrategy.criteria().map(spec -> {
            logger.debug("Using ACL JPA specification for objects '{}': {}", getDomainClass().getSimpleName(), spec);
            return Specification.where(spec);
        }).orElse(Specification.where(null));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "<" + getDomainClass().getSimpleName() + ">";
    }
}