package io.github.aliothliu.marble.rbac.domain;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import static org.junit.jupiter.api.Assertions.*;

class ElementTest {

    @Test
    void isAllow() {
        Element element = Element.builder()
                .name("btn-create")
                .readableName("新建")
                .api(new Api(HttpMethod.POST, "/element"))
                .build();

        assertFalse(element.isAllow(new Api(HttpMethod.GET, "/element")));
    }
}