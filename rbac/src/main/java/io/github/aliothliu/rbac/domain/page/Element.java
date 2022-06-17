package io.github.aliothliu.rbac.domain.page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Objects;

@Builder
@Getter
@Entity
@Table(name = "#{rbacProperties.jpa.pageElementTableName}")
@AllArgsConstructor
@EqualsAndHashCode
public class Element {

    @Id
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "uuid")
    private String id;

    @Column(name = "name", length = 128, nullable = false)
    private String name;

    @Column(name = "readable_name", length = 128, nullable = false)
    private String readableName;

    @Embedded
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
