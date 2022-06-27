package io.github.aliothliu.rbac.infrastructure.events;

import io.github.aliothliu.rbac.domain.DomainEvent;
import io.github.aliothliu.rbac.domain.DomainEventPublisher;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * @author Alioth Liu
 **/
@Component
public class DefaultDomainEventPublisher implements DomainEventPublisher, ApplicationContextAware {
    private static ApplicationContext applicationContext = null;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        DefaultDomainEventPublisher.applicationContext = applicationContext;
    }

    @Override
    public <T extends DomainEvent> void publish(T t) {
        if (applicationContext != null) {
            applicationContext.publishEvent(t);
        }
    }
}
