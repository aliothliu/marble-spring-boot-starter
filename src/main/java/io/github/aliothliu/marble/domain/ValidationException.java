package io.github.aliothliu.marble.domain;

import org.springframework.http.HttpStatus;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.spring.common.HttpStatusAdapter;

import java.net.URI;

/**
 * @author liubin
 */
public class ValidationException extends AbstractThrowableProblem {

    public ValidationException(String message) {
        this(URI.create("/validation-exception"), message);
    }

    public ValidationException(URI uri, String message) {
        super(uri, "参数校验失败", new HttpStatusAdapter(HttpStatus.BAD_REQUEST), message);
    }
}
