package io.github.aliothliu.rbac.infrastructure.errors;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

/**
 * @author Alioth Liu
 **/
public class RoleGrantException extends AbstractThrowableProblem {

    public RoleGrantException(String detail) {
        super(URI.create("/role-grant-error"), "failed to grant policy ", Status.INTERNAL_SERVER_ERROR, detail);
    }
}
