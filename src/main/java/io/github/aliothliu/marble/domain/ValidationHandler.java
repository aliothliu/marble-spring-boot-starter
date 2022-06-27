package io.github.aliothliu.marble.domain;

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
