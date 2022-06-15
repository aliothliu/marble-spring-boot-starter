package io.github.aliothliu.marble.rbac;

import org.zalando.problem.AbstractThrowableProblem;

import java.net.URI;

/**
 * 参数校验异常
 *
 * @author vincent
 */
public class AssertionException extends AbstractThrowableProblem {
    public AssertionException(String message) {
        super(URI.create("/assertion-exception"), message);
    }
}