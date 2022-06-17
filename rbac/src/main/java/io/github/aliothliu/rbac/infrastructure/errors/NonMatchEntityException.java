package io.github.aliothliu.rbac.infrastructure.errors;

import io.github.aliothliu.rbac.domain.ValidationException;

public class NonMatchEntityException extends ValidationException {

    public NonMatchEntityException(String message) {
        super(message);
    }
}
