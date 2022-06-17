package io.github.aliothliu.rbac.domain.page;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Api {

    @Column(name = "method", length = 128)
    private String method;

    @Column(name = "url")
    private String url;
}
