package io.github.aliothliu.marble.domain;

import java.net.URI;

/**
 * 参数校验异常
 *
 * @author vincent
 */
public class AssertionException extends ValidationException {
    public AssertionException(String message) {
        super(URI.create("/assertion-exception"), message);
    }
}