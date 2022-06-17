package io.github.aliothliu.rbac;

import io.github.aliothliu.rbac.application.*;
import io.github.aliothliu.rbac.domain.menu.MenuPathRepository;
import io.github.aliothliu.rbac.domain.menu.MenuRepository;
import io.github.aliothliu.rbac.domain.menu.MenuSortService;
import io.github.aliothliu.rbac.domain.page.PageRepository;
import io.github.aliothliu.rbac.domain.role.RoleRepository;
import io.github.aliothliu.rbac.infrastructure.casbin.CasbinRuleRepository;
import io.github.aliothliu.rbac.infrastructure.casbin.SpringDataJpaAdapter;
import io.github.aliothliu.rbac.infrastructure.jpa.ConfigurableSpringPhysicalNamingStrategy;
import org.casbin.jcasbin.main.Enforcer;
import org.casbin.jcasbin.persist.Adapter;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackageClasses = RbacConfiguration.class)
public class RbacConfiguration {

    @Bean
    public Adapter springDataJpaAdapter(final CasbinRuleRepository repository,
                                        final JdbcTemplate jdbcTemplate) {
        return new SpringDataJpaAdapter(repository, jdbcTemplate);
    }

    @Bean
    public PhysicalNamingStrategy configurableSpringPhysicalNamingStrategy() {
        return new ConfigurableSpringPhysicalNamingStrategy();
    }

    @Bean
    @ConditionalOnMissingBean
    public RbacPageService rbacPageManager(final PageRepository repository) {
        return new RbacPageServiceImpl(repository);
    }

    @Bean
    @ConditionalOnMissingBean
    public RbacMenuService rbacMenuManager(final MenuRepository repository,
                                           final MenuPathRepository pathRepository,
                                           final JdbcTemplate jdbcTemplate,
                                           final PageRepository pageRepository,
                                           final RbacRoleService rbacRoleService,
                                           final MenuSortService sortService,
                                           final RbacProperties rbacProperties) {
        return new RbacMenuServiceImpl(repository, pathRepository, jdbcTemplate, pageRepository, (RbacRoleServiceImpl) rbacRoleService, sortService, rbacProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public RbacRoleService rbacRoleManager(final RoleRepository repository,
                                           final MenuRepository menuRepository,
                                           final PageRepository pageRepository,
                                           final Enforcer enforcer) {
        return new RbacRoleServiceImpl(repository, menuRepository, pageRepository, enforcer);
    }
}
