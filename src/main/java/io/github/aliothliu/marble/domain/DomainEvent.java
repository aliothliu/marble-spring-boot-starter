package io.github.aliothliu.marble.domain;

import org.springframework.context.ApplicationEvent;

/**
 * 领域事件
 *
 * @author liubin
 */
public abstract class DomainEvent extends ApplicationEvent {

    public DomainEvent(Object source) {
        super(source);
    }

    /**
     * 获取事件版本
     *
     * @return 事件版本
     */
    public abstract int eventVersion();
}
