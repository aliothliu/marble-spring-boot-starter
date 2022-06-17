package io.github.aliothliu.rbac.domain;

/**
 * 快速失败异常处理器
 *
 * @author liubin
 */
public class FailFastValidationHandler implements ValidationHandler {

    @Override
    public void handleException(ValidationException exception) {
        throw exception;
    }
}
