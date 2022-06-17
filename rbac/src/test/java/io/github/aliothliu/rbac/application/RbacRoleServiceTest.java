package io.github.aliothliu.rbac.application;

import io.github.aliothliu.rbac.application.representation.RoleRepresentation;
import io.github.aliothliu.rbac.domain.role.RoleCode;
import io.github.aliothliu.rbac.domain.role.Status;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class RbacRoleServiceTest {

    @Autowired
    private RbacRoleService roleService;

    @Autowired
    private RbacPageService pageService;

    @Autowired
    private RbacMenuService menuManager;

    @Test
    void paging() {
        for (int i = 1; i <= 36; i++) {
            this.roleService.newRole(new RoleCode("role-" + i), "分页测试角色" + i, null);
        }
        Page<RoleRepresentation> page = this.roleService.paging("分页测试角色", Pageable.unpaged());
        assertEquals(36, page.getTotalElements());
    }

    @Test
    void getOne() {
        RoleCode roleCode = this.roleService.newRole(new RoleCode("role-code"), "Administrator", "统一管理系统全部权限");
        assertTrue(this.roleService.getOne(roleCode).isPresent());
        this.roleService.getOne(roleCode).ifPresent(r -> {
            Assertions.assertEquals("Administrator", r.getName());
            Assertions.assertEquals("统一管理系统全部权限", r.getDescription());
            Assertions.assertEquals(Status.ENABLE.name(), r.getStatus());
            Assertions.assertEquals(Status.ENABLE.getDescription(), r.getStatusDesc());
        });
    }

    @Test
    void changeRole() {
    }

    @Test
    void hasMenu() {
    }

    @Test
    void hasPage() {
    }

    @Test
    void hasElement() {
    }

    @Test
    void grant() {
    }

    @Test
    void testGrant() {
    }

    @Test
    void testGrant1() {
    }

    @Test
    void loadMenus() {
    }

    @Test
    void loadPages() {
    }

    @Test
    void loadElements() {
    }
}