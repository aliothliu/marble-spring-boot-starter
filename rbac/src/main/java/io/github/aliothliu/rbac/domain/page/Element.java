package io.github.aliothliu.rbac.domain.page;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Builder
@Getter
@Entity
@Table(name = "#{rbacProperties.jpa.pageElementTableName}")
@AllArgsConstructor
@EqualsAndHashCode
@RequiredArgsConstructor
public class Element {

    @EmbeddedId
    @NonNull
    private ElementId id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "page_id", length = 40))
    })
    private PageId pageId;

    @Column(name = "name", length = 128, nullable = false)
    @NonNull
    private String name;

    @Column(name = "readable_name", length = 128, nullable = false)
    @NonNull
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
