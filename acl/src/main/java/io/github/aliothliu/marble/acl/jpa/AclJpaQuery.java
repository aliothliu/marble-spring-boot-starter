package io.github.aliothliu.marble.acl.jpa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.PartTreeJpaQuery;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class AclJpaQuery implements RepositoryQuery {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final RepositoryQuery query;
    private final EntityManager em;
    private final JpaAclStrategy<Object> aclStrategy;
    private final Method method;
    private CriteriaQuery<?> cachedCriteriaQuery;
    private Root<Object> root;

    public AclJpaQuery(Method method, RepositoryQuery query, EntityManager em, JpaAclStrategy<?> aclStrategy) {
        this.method = method;
        this.query = query;
        this.em = em;
        this.aclStrategy = (JpaAclStrategy<Object>) aclStrategy;
        this.initAcl();
    }

    @SuppressWarnings("unchecked")
    private static <T> T getField(Class<?> type, Object object, String fieldName) {
        Field field = ReflectionUtils.findField(type, fieldName);
        assert field != null;
        field.setAccessible(true);
        Object property = ReflectionUtils.getField(field, object);
        return (T) property;
    }

    @Override
    @Nullable
    public Object execute(@NonNull Object[] parameters) {
        if (cachedCriteriaQuery == null) {
            return query.execute(parameters);
        }
        Specification<Object> specification = aclStrategy.criteria().orElse(null);
        Predicate aclPredicate = specification.toPredicate(root, cachedCriteriaQuery, em.getCriteriaBuilder());
        cachedCriteriaQuery.where(em.getCriteriaBuilder().and(cachedCriteriaQuery.getRestriction(), aclPredicate));
        return query.execute(parameters);
    }

    @NonNull
    @Override
    public QueryMethod getQueryMethod() {
        return query.getQueryMethod();
    }

    private void initAcl() {
        CriteriaQuery<?> criteriaQuery = criteriaQuery();
        if (criteriaQuery == null) {
            logger.warn("Unable to install ACL Jpa Specification for method '" + method
                    + "' and query: " + query + " ; query methods with Pageable/Sort are not (yet) supported");
            return;
        }
        this.cachedCriteriaQuery = criteriaQuery;
        this.root = root(cachedCriteriaQuery);
    }

    private CriteriaQuery<?> criteriaQuery() {
        Object queryPreparer = getField(PartTreeJpaQuery.class, query, "query");
        return getField(queryPreparer.getClass(), queryPreparer, "cachedCriteriaQuery");
    }

    @SuppressWarnings("unchecked")
    private Root<Object> root(CriteriaQuery<?> criteriaQuery) {
        return (Root<Object>) criteriaQuery.getRoots().iterator().next();
    }
}
