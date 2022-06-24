package io.github.aliothliu.marble.acl;

import io.github.aliothliu.marble.acl.core.AclStrategy;
import io.github.aliothliu.marble.acl.core.AclStrategyFactory;
import io.github.aliothliu.marble.acl.core.DefaultAclStrategyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import javax.annotation.Resource;
import java.util.Map;

@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackageClasses = AclConfiguration.class)
public class AclConfiguration {

    private final Logger logger = LoggerFactory.getLogger(AclConfiguration.class);
    @Resource
    private ApplicationContext applicationContext;

    @Bean
    @ConditionalOnMissingBean(AclStrategyFactory.class)
    public AclStrategyFactory aclStrategyFactory(BeanFactory beanFactory) {
        return new DefaultAclStrategyFactory(beanFactory);
    }

    @EventListener(ContextRefreshedEvent.class)
    public void logStrategies() {
        Map<String, AclStrategy> strategies = applicationContext.getBeansOfType(AclStrategy.class);
        strategies.forEach((key, value) -> logger.debug("Strategy {}: {}", key, value));
    }
}
