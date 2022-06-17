package io.github.aliothliu.rbac.domain.menu;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "#{rbacProperties.jpa.menuPathTableName}")
@FieldNameConstants
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MenuPath {

    @Id
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "uuid")
    @Setter
    private String id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "ancestor"))
    })
    private MenuId ancestor;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "descendant"))
    })
    private MenuId descendant;

    private int distance = 0;

    public MenuPath(MenuId ancestor, MenuId descendant, int distance) {
        this.ancestor = ancestor;
        this.descendant = descendant;
        this.distance = distance;
    }

    public void increaseDistance() {
        this.distance = this.distance + 1;
    }
}
