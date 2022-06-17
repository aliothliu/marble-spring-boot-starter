package io.github.aliothliu.rbac.domain;

/**
 * @author liubin
 */
public interface ValidationHandler {
    /**
     * 处理验证异常
     *
     * @param exception 验证异常
     */
    void handleException(ValidationException exception);
}
