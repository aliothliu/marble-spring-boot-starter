package io.github.aliothliu.marble.infrastructure.jpa;

import io.github.aliothliu.marble.infrastructure.acl.Acl;
import io.github.aliothliu.marble.infrastructure.acl.AclStrategy;
import io.github.aliothliu.marble.infrastructure.acl.AclStrategyFactory;
import io.github.aliothliu.marble.infrastructure.jpa.annotation.NoAcl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.query.PartTreeJpaQuery;
import org.springframework.data.jpa.repository.support.*;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Optional;

public class AclJpaRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable>
        extends JpaRepositoryFactoryBean<T, S, ID> {

    @Resource
    private AclStrategyFactory strategyFactory;

    public AclJpaRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    @NonNull
    protected RepositoryFactorySupport createRepositoryFactory(@NonNull EntityManager entityManager) {
        return new Factory(entityManager);
    }

    private class Factory extends JpaRepositoryFactory {

        private final Logger logger = LoggerFactory.getLogger(getClass());
        private final EntityManager em;

        public Factory(EntityManager entityManager) {
            super(entityManager);
            em = entityManager;
        }

        @NonNull
        @Override
        protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
            return withoutAcl(metadata.getDomainType()) //
                    ? super.getRepositoryBaseClass(metadata) //
                    : AclJpaRepository.class;
        }

        @NonNull
        @Override
        protected Optional<QueryLookupStrategy> getQueryLookupStrategy(@Nullable QueryLookupStrategy.Key key, @NonNull QueryMethodEvaluationContextProvider evaluationContextProvider) {
            return Optional.of(new AclQueryLookupStrategy(key, evaluationContextProvider));
        }

        @Override
        @NonNull
        protected JpaRepositoryImplementation<?, ?> getTargetRepository(RepositoryInformation information,
                                                                        @NonNull EntityManager entityManager) {
            Class<?> domainType = information.getDomainType();
            if (withoutAcl(domainType)) {
                this.logger.info("The domain type of -> {} without Acl annotation, all the acl strategy will expired", domainType.getName());
                return super.getTargetRepository(information, entityManager);
            }

            JpaEntityInformation<?, Serializable> entityInformation = getEntityInformation(domainType);

            SimpleJpaRepository<?, ?> repository = getTargetRepositoryViaReflection(information,
                    entityInformation, entityManager, this.getStrategy(domainType));
            logger.debug("Created {}", repository);

            return repository;
        }

        private boolean withoutAcl(Class<?> domainType) {
            return domainType.getDeclaredAnnotation(NoAcl.class) != null || domainType.getDeclaredAnnotation(Acl.class) == null;
        }

        private JpaAclStrategy<?> getStrategy(Class<?> domainType) {
            Acl acl = domainType.getDeclaredAnnotation(Acl.class);
            AclStrategy<?> aclStrategy = strategyFactory.getStrategy(acl.strategy());
            if (aclStrategy instanceof JpaAclStrategy) {
                return (JpaAclStrategy<?>) aclStrategy;
            }
            return new JpaAllowAllStrategy<>();
        }

        private class AclQueryLookupStrategy implements QueryLookupStrategy {
            private final Key key;
            private final QueryMethodEvaluationContextProvider evaluationContextProvider;

            public AclQueryLookupStrategy(Key key, QueryMethodEvaluationContextProvider evaluationContextProvider) {
                this.key = key;
                this.evaluationContextProvider = evaluationContextProvider;
            }

            @Override
            @NonNull
            @SuppressWarnings("unused")
            public RepositoryQuery resolveQuery(@NonNull Method method,
                                                @NonNull RepositoryMetadata repositoryMetadata,
                                                @NonNull ProjectionFactory projectionFactory,
                                                @NonNull NamedQueries namedQueries) {
                QueryLookupStrategy queryLookupStrategy = Factory.super.getQueryLookupStrategy(key, evaluationContextProvider).orElseThrow(NullPointerException::new);

                RepositoryQuery query = queryLookupStrategy.resolveQuery(method, repositoryMetadata, projectionFactory, namedQueries);
                return wrapQuery(method, repositoryMetadata, query);
            }

            private RepositoryQuery wrapQuery(Method method,
                                              RepositoryMetadata metadata,
                                              RepositoryQuery query) {
                if (withoutAcl(metadata.getDomainType())) {
                    // no acl applied here
                    return query;
                }
                if (query instanceof PartTreeJpaQuery) {
                    query = new AclJpaQuery(method, query, em, getStrategy(metadata.getDomainType()));
                } else {
                    logger.warn("Unsupported query type for method '{}' > ACL Jpa Specification not installed: {}", method, query.getClass());
                }
                return query;
            }
        }
    }
}
