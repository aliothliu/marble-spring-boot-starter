package io.github.aliothliu.marble.infrastructure.errors;

import io.github.aliothliu.marble.domain.ValidationException;

public class NonMatchEntityException extends ValidationException {

    public NonMatchEntityException(String message) {
        super(message);
    }
}
