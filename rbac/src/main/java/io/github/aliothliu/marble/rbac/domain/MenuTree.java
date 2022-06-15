package io.github.aliothliu.marble.rbac.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@FieldNameConstants
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MenuTree {

    @Setter
    private String id;

    private MenuId ancestor;

    private MenuId descendant;

    private int distance = 0;

    public MenuTree(MenuId ancestor, MenuId descendant, int distance) {
        this.ancestor = ancestor;
        this.descendant = descendant;
        this.distance = distance;
    }

    public void increaseDistance() {
        this.distance = this.distance + 1;
    }
}
