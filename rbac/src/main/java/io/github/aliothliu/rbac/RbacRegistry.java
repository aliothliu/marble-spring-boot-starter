package io.github.aliothliu.rbac;

import io.github.aliothliu.rbac.application.RbacMenuService;
import io.github.aliothliu.rbac.domain.menu.MenuPathRepository;
import io.github.aliothliu.rbac.domain.menu.MenuRepository;
import io.github.aliothliu.rbac.domain.page.PageRepository;
import io.github.aliothliu.rbac.domain.role.RoleRepository;
import org.casbin.jcasbin.main.Enforcer;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public final class RbacRegistry implements ApplicationContextAware {

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

    public static Enforcer enforcer() {
        return applicationContext.getBean(Enforcer.class);
    }

    public static RbacMenuService menuManager() {
        return applicationContext.getBean(RbacMenuService.class);
    }

    public static RbacProperties rbacProperties() {
        return applicationContext.getBean(RbacProperties.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        RbacRegistry.applicationContext = applicationContext;
    }
}
