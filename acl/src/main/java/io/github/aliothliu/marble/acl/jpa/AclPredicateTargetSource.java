package io.github.aliothliu.marble.acl.jpa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.TargetSource;
import org.springframework.lang.NonNull;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

public class AclPredicateTargetSource implements TargetSource {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Predicate original;
    private final CriteriaBuilder criteriaBuilder;
    private Predicate current;

    public AclPredicateTargetSource(CriteriaBuilder criteriaBuilder, Predicate original) {
        this.criteriaBuilder = criteriaBuilder;
        this.original = original;
        setCurrent(original);
        logger.debug("Original predicate : {}", original);
    }

    public void installAcl(Predicate aclPredicate) {
        Predicate enhancedPredicate = criteriaBuilder.and(original, aclPredicate);
        setCurrent(enhancedPredicate);
        logger.debug("Enhanced predicate : {}", enhancedPredicate);
    }

    public void uninstallAcl() {
        setCurrent(original);
    }

    @Override
    public Class<?> getTargetClass() {
        return getTarget().getClass();
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public Object getTarget() {
        return current;
    }

    @Override
    public void releaseTarget(@NonNull Object target) throws Exception {
    }

    private void setCurrent(Predicate predicate) {
        this.current = predicate;
    }
}
