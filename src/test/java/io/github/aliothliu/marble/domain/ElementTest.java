package io.github.aliothliu.marble.domain;

import io.github.aliothliu.marble.domain.page.Api;
import io.github.aliothliu.marble.domain.page.Element;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import static org.junit.jupiter.api.Assertions.assertFalse;

class ElementTest {

    @Test
    void isAllow() {
        Element element = Element.builder()
                .name("btn-create")
                .readableName("新建")
                .api(new Api(HttpMethod.POST.name(), "/element"))
                .build();

        assertFalse(element.isAllow(new Api(HttpMethod.GET.name(), "/element")));
    }
}