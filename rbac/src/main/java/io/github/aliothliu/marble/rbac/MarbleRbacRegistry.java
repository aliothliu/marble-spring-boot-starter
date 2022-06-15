package io.github.aliothliu.marble.rbac;

import io.github.aliothliu.marble.rbac.domain.MenuRepository;
import io.github.aliothliu.marble.rbac.domain.PageRepository;
import io.github.aliothliu.marble.rbac.domain.RoleRepository;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class MarbleRbacRegistry implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public static MenuRepository menuRepository() {
        return applicationContext.getBean(MenuRepository.class);
    }

    public static PageRepository pageRepository() {
        return applicationContext.getBean(PageRepository.class);
    }

    public static RoleRepository roleRepository() {
        return applicationContext.getBean(RoleRepository.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        MarbleRbacRegistry.applicationContext = applicationContext;
    }
}
