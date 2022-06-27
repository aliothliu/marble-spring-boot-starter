package io.github.aliothliu.marble;

import io.github.aliothliu.marble.application.RbacMenuService;
import io.github.aliothliu.marble.domain.DomainEventPublisher;
import io.github.aliothliu.marble.domain.menu.MenuPathRepository;
import io.github.aliothliu.marble.domain.menu.MenuRepository;
import io.github.aliothliu.marble.domain.page.ElementRepository;
import io.github.aliothliu.marble.domain.page.PageRepository;
import io.github.aliothliu.marble.domain.role.RoleRepository;
import org.casbin.jcasbin.main.Enforcer;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public final class MarbleRegistry implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public static MenuRepository menuRepository() {
        return applicationContext.getBean(MenuRepository.class);
    }

    public static MenuPathRepository menuPathRepository() {
        return applicationContext.getBean(MenuPathRepository.class);
    }

    public static PageRepository pageRepository() {
        return applicationContext.getBean(PageRepository.class);
    }

    public static RoleRepository roleRepository() {
        return applicationContext.getBean(RoleRepository.class);
    }

    public static ElementRepository elementRepository() {
        return applicationContext.getBean(ElementRepository.class);
    }

    public static Enforcer enforcer() {
        return applicationContext.getBean(Enforcer.class);
    }

    public static RbacMenuService menuManager() {
        return applicationContext.getBean(RbacMenuService.class);
    }

    public static MarbleProperties marbleProperties() {
        return applicationContext.getBean(MarbleProperties.class);
    }

    public static DomainEventPublisher eventPublisher() {
        return applicationContext.getBean(DomainEventPublisher.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        MarbleRegistry.applicationContext = applicationContext;
    }
}
