package io.github.aliothliu.marble.rbac.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpMethod;

import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Api {

    private HttpMethod method;

    private String url;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Api)) {
            return false;
        }
        Api api = (Api) o;
        return method == api.method &&
                Objects.equals(url, api.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, url);
    }
}
