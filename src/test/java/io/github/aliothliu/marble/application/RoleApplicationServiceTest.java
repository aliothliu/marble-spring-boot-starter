package io.github.aliothliu.marble.application;

import io.github.aliothliu.marble.application.command.ChangeRoleCommand;
import io.github.aliothliu.marble.application.command.GrantElementCommand;
import io.github.aliothliu.marble.application.command.GrantMenuCommand;
import io.github.aliothliu.marble.application.command.NewRoleCommand;
import io.github.aliothliu.marble.domain.Sort;
import io.github.aliothliu.marble.domain.menu.Menu;
import io.github.aliothliu.marble.domain.menu.MenuId;
import io.github.aliothliu.marble.domain.menu.MenuRepository;
import io.github.aliothliu.marble.domain.page.*;
import io.github.aliothliu.marble.domain.role.Role;
import io.github.aliothliu.marble.domain.role.RoleCode;
import io.github.aliothliu.marble.domain.role.RoleRepository;
import io.github.aliothliu.marble.domain.role.Type;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class RoleApplicationServiceTest {

    @Autowired
    private RoleApplicationService applicationService;

    @Autowired
    private RoleRepository roleRepository;

    @MockBean
    private MenuRepository menuRepository;

    @MockBean
    private ElementRepository elementRepository;

    @BeforeEach
    public void setup() {
        NewRoleCommand command = new NewRoleCommand();
        command.setName("administrator");
        command.setCode("admin");
        command.setType(Type.Super);
        this.applicationService.newRole(command);
    }

    @AfterEach
    public void teardown() {
        this.roleRepository.deleteAll();
    }

    @Test
    void isSuper() {
        Role role = this.roleRepository.findByCode(new RoleCode("admin")).orElse(null);

        assertNotNull(role);

        assertTrue(role.isSuper());
    }

    @Test
    void changeRole() {
        ChangeRoleCommand command = new ChangeRoleCommand();
        command.setName("auditor");
        command.setDescription("审计");
        command.setType(Type.Standard);
        this.applicationService.changeRole(new RoleCode("admin"), command);

        assertEquals("auditor", command.getName());
        assertEquals("审计", command.getDescription());
        assertEquals(Type.Standard, command.getType());
    }

    @Test
    void grantMenu() {
        Role role = this.roleRepository.findByCode(new RoleCode("admin")).orElse(null);

        assertNotNull(role);

        assertFalse(role.hasMenu(new MenuId("dashboard")));

        Menu dashboard = Menu.builder().menuId(new MenuId("dashboard")).name("首页").pageId(new PageId("dashboard")).icon("dashboard").sort(Sort.one()).build();
        Menu system = Menu.builder().menuId(new MenuId("system")).name("系统管理").pageId(new PageId("system")).icon("system").sort(Sort.one()).build();

        Mockito.when(this.menuRepository.findAllById(Collections.singleton(new MenuId("dashboard")))).thenReturn(Collections.singletonList(dashboard));

        GrantMenuCommand command = new GrantMenuCommand();
        command.setMenus(Collections.singletonList("dashboard"));
        // grant single menu
        this.applicationService.grant(new RoleCode("admin"), command);
        assertTrue(role.hasMenu(new MenuId("dashboard")));
        assertFalse(role.hasMenu(new MenuId("system")));
        assertTrue(role.hasPage(new PageId("dashboard")));
        assertFalse(role.hasPage(new PageId("system")));

        assertEquals(1, role.loadMenus().size());
        assertTrue(role.loadMenus().contains(new MenuId("dashboard")));

        // grant all menu
        Set<MenuId> menus = new HashSet<>();
        menus.add(new MenuId("dashboard"));
        menus.add(new MenuId("system"));

        command.setMenus(menus.stream().map(MenuId::getId).collect(Collectors.toList()));

        Mockito.when(this.menuRepository.findAllById(menus)).thenReturn(Arrays.asList(dashboard, system));

        this.applicationService.grant(new RoleCode("admin"), command);
        assertTrue(role.hasMenu(new MenuId("dashboard")));
        assertTrue(role.hasMenu(new MenuId("system")));

        assertEquals(2, role.loadMenus().size());
        assertTrue(role.loadMenus().contains(new MenuId("dashboard")));
        assertTrue(role.loadMenus().contains(new MenuId("system")));

        // grant single menu again
        Mockito.when(this.menuRepository.findAllById(Collections.singleton(new MenuId("system")))).thenReturn(Collections.singletonList(system));

        command.setMenus(Collections.singletonList("system"));
        this.applicationService.grant(new RoleCode("admin"), command);
        assertFalse(role.hasMenu(new MenuId("dashboard")));
        assertTrue(role.hasMenu(new MenuId("system")));

        assertEquals(1, role.loadMenus().size());
        assertTrue(role.loadMenus().contains(new MenuId("system")));
    }

    @Test
    void grantElements() {
        Role role = this.roleRepository.findByCode(new RoleCode("admin")).orElse(null);
        assertNotNull(role);

        PageId pageId = new PageId("role");

        Element detail = Element.builder().id(new ElementId("detail")).pageId(pageId).name("detail").readableName("详情").api(new Api(HttpMethod.GET.name(), "/roles/{id}")).build();

        Mockito.when(this.elementRepository.findAllById(Collections.singleton(new ElementId("detail")))).thenReturn(Collections.singletonList(detail));
        Mockito.when(this.elementRepository.findById(new ElementId("detail"))).thenReturn(Optional.of(detail));

        GrantElementCommand command = new GrantElementCommand();
        command.setElements(Collections.singletonList("detail"));
        this.applicationService.grant(new RoleCode("admin"), command);

        assertTrue(role.hasElement(new ElementId("detail")));
    }
}