package io.github.aliothliu.rbac.domain.menu;

import io.github.aliothliu.rbac.domain.DomainEvent;
import io.github.aliothliu.rbac.domain.page.PageId;
import lombok.Getter;

/**
 * @author Alioth Liu
 **/
@Getter
public class MenuCreated extends DomainEvent {

    private final MenuId id;

    private final PageId pageId;

    public MenuCreated(Object source, MenuId id, PageId pageId) {
        super(source);
        this.id = id;
        this.pageId = pageId;
    }

    @Override
    public int eventVersion() {
        return 0;
    }
}
