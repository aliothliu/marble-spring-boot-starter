package io.github.aliothliu.marble.rbac.domain;

import lombok.*;

import java.util.Objects;

@Builder
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class Element {

    private String id;

    @NonNull
    private String name;

    @NonNull
    private String readableName;

    private Api api;

    protected Element() {

    }

    public boolean isAllow(Api api) {
        if (Objects.isNull(this.api)) {
            return false;
        }
        return this.api.equals(api);
    }
}
