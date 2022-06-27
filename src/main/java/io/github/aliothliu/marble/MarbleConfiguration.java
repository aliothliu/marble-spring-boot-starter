package io.github.aliothliu.marble;

import io.github.aliothliu.marble.application.RbacMenuService;
import io.github.aliothliu.marble.application.RbacMenuServiceImpl;
import io.github.aliothliu.marble.application.RbacPageService;
import io.github.aliothliu.marble.application.RbacPageServiceImpl;
import io.github.aliothliu.marble.domain.menu.MenuPathRepository;
import io.github.aliothliu.marble.domain.menu.MenuRepository;
import io.github.aliothliu.marble.domain.menu.MenuSortService;
import io.github.aliothliu.marble.domain.page.PageRepository;
import io.github.aliothliu.marble.infrastructure.acl.AclStrategy;
import io.github.aliothliu.marble.infrastructure.acl.AclStrategyFactory;
import io.github.aliothliu.marble.infrastructure.acl.DefaultAclStrategyFactory;
import io.github.aliothliu.marble.infrastructure.casbin.CasbinRuleRepository;
import io.github.aliothliu.marble.infrastructure.casbin.FileUtil;
import io.github.aliothliu.marble.infrastructure.casbin.SpringDataJpaAdapter;
import io.github.aliothliu.marble.infrastructure.jpa.ConfigurableSpringPhysicalNamingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.casbin.jcasbin.main.Enforcer;
import org.casbin.jcasbin.main.SyncedEnforcer;
import org.casbin.jcasbin.model.Model;
import org.casbin.jcasbin.persist.Adapter;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackageClasses = MarbleConfiguration.class)
@Slf4j
public class MarbleConfiguration {

    @Resource
    private ApplicationContext applicationContext;

    @Bean
    public Adapter springDataJpaAdapter(final CasbinRuleRepository repository,
                                        final JdbcTemplate jdbcTemplate) {
        return new SpringDataJpaAdapter(repository, jdbcTemplate);
    }

    /**
     * Automatic configuration of the enforcer
     */
    @Bean
    @ConditionalOnMissingBean
    public Enforcer enforcer(Adapter adapter) {
        Model model = new Model();
        String modelContext = FileUtil.getFileAsText("classpath:casbin/model.conf");
        if (Objects.nonNull(modelContext)) {
            model.loadModelFromText(modelContext);
        } else {
            log.warn("加载策略文件失败，配置默认的RBAC模型");
            // request definition
            model.addDef("r", "r", "sub, obj, act");
            // policy definition
            model.addDef("p", "p", "sub, obj, act");
            // role definition
            model.addDef("g", "g", "_, _");
            // policy effect
            model.addDef("e", "e", "some(where (p.eft == allow))");
            // matchers
            model.addDef("m", "m", "g(r.sub, p.sub) && r.obj == p.obj && r.act == p.act");
        }
        Enforcer enforcer = new SyncedEnforcer(model, adapter);
        enforcer.enableAutoSave(true);
        return enforcer;
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
                                           final MenuSortService sortService,
                                           final MarbleProperties marbleProperties) {
        return new RbacMenuServiceImpl(repository, pathRepository, jdbcTemplate, pageRepository, sortService, marbleProperties);
    }

    @Bean
    @ConditionalOnMissingBean(AclStrategyFactory.class)
    public AclStrategyFactory aclStrategyFactory(BeanFactory beanFactory) {
        return new DefaultAclStrategyFactory(beanFactory);
    }

    @EventListener(ContextRefreshedEvent.class)
    public void logStrategies() {
        Map<String, AclStrategy> strategies = applicationContext.getBeansOfType(AclStrategy.class);
        strategies.forEach((key, value) -> log.debug("Strategy {}: {}", key, value));
    }
}
