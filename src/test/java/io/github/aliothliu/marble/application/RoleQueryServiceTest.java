package io.github.aliothliu.marble.application;

import io.github.aliothliu.marble.application.command.NewRoleCommand;
import io.github.aliothliu.marble.application.representation.RoleDetailRepresentation;
import io.github.aliothliu.marble.application.representation.RoleRepresentation;
import io.github.aliothliu.marble.domain.role.RoleCode;
import io.github.aliothliu.marble.domain.role.RoleRepository;
import io.github.aliothliu.marble.domain.role.Status;
import io.github.aliothliu.marble.domain.role.Type;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("h2")
class RoleQueryServiceTest {

    @Autowired
    private RoleApplicationService applicationService;

    @Autowired
    private RoleQueryService roleQueryService;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    public void setup() {
        for (int i = 1; i <= 50; i++) {
            NewRoleCommand command = new NewRoleCommand();
            command.setName("分页测试角色-" + i);
            command.setCode("role-" + i);
            command.setType(Type.Standard);
            this.applicationService.newRole(command);
        }
    }

    @AfterEach
    public void teardown() {
        this.roleRepository.deleteAll();
    }

    @Test
    void paging() {
        Page<RoleRepresentation> page = this.roleQueryService.paging("分页测试角色", Pageable.unpaged());
        assertEquals(50, page.getTotalElements());
    }

    @Test
    void getOne() {
        RoleDetailRepresentation representation = this.roleQueryService.getOne(new RoleCode("role-1")).orElse(null);

        assertNotNull(representation);

        Assertions.assertEquals("分页测试角色-1", representation.getName());
        Assertions.assertNull(representation.getDescription());
        Assertions.assertEquals(Status.ENABLE.name(), representation.getStatus());
        Assertions.assertEquals(Status.ENABLE.getDescription(), representation.getStatusDesc());
    }
}