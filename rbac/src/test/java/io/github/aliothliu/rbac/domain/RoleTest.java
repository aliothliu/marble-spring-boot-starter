package io.github.aliothliu.rbac.domain;

import io.github.aliothliu.rbac.RbacRegistry;
import io.github.aliothliu.rbac.domain.role.Role;
import io.github.aliothliu.rbac.domain.role.RoleCode;
import io.github.aliothliu.rbac.domain.role.RoleRepository;
import io.github.aliothliu.rbac.domain.role.Status;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(classes = RbacRegistry.class)
class RoleTest {

    @MockBean
    private RoleRepository roleRepository;

    @Test
    void validateFail() {
        Mockito.when(this.roleRepository.existsByCode(new RoleCode("Test Engineer"))).thenReturn(Boolean.TRUE);

        Role role = new Role(new RoleCode("Test Engineer"), "测试工程师");

        Exception exception = assertThrows(AssertionException.class, role::validate);

        assertTrue(exception.getMessage().contains("不允许重复的角色编码"));
    }

    @Test
    void test() {
        Mockito.when(this.roleRepository.existsByCode(new RoleCode("Test Engineer"))).thenReturn(Boolean.FALSE);

        Role role = new Role(new RoleCode("Test Engineer"), "测试工程师");

        assertEquals("Test Engineer", role.getCode().getCode());
        assertEquals("测试工程师", role.getName());
        assertEquals(Status.ENABLE, role.getStatus());

        role.changeName("中级测试工程师");
        role.changeDescription("负责应用功能测试");
        role.changeStatus(Status.DISABLE);

        assertEquals("中级测试工程师", role.getName());
        assertEquals("负责应用功能测试", role.getDescription());
        assertEquals(Status.DISABLE, role.getStatus());
    }
}