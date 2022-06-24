package io.github.aliothliu.marble.acl.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

public class DefaultAclStrategyFactory implements AclStrategyFactory {

    private final Logger logger = LoggerFactory.getLogger(DefaultAclStrategyFactory.class);

    private final BeanFactory beanFactory;

    public DefaultAclStrategyFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public <T extends AclStrategy<?>> T getStrategy(Class<T> clazz) {
        try {
            return beanFactory.getBean(clazz);
        } catch (NoSuchBeanDefinitionException e) {
            logger.warn("Unable to find strategy bean with class '{}' > fall back on default strategy", clazz.getName());
        }
        return null;
    }
}
