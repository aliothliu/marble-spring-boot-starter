package io.github.aliothliu.marble.domain;

/**
 * @author liubin
 **/
public interface DomainEventPublisher {

    <T extends DomainEvent> void publish(T event);
}
