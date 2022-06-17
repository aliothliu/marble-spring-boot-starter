package io.github.aliothliu.rbac.domain;

/**
 * @author liubin
 **/
public interface DomainEventPublisher {

    <T extends DomainEvent> void publish(T event);
}
