package io.github.aliothliu.rbac.domain;

/**
 * 领域验证接口
 *
 * @param <T>
 * @author liubin
 */
public abstract class Validator<T> {

    private final ValidationHandler handler;

    public Validator(ValidationHandler handler) {
        this.handler = handler;
    }

    /**
     * 验证
     *
     * @param arg 验证参数
     */
    public abstract void validate(T arg);

    /**
     * 获取异常处理器
     *
     * @return 异常处理器
     */
    protected ValidationHandler notificationHandler() {
        return this.handler;
    }
}