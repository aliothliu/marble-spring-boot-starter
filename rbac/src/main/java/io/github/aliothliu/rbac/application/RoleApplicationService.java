package io.github.aliothliu.rbac.application;

import io.github.aliothliu.rbac.application.command.ChangeRoleCommand;
import io.github.aliothliu.rbac.application.command.GrantElementCommand;
import io.github.aliothliu.rbac.application.command.GrantMenuCommand;
import io.github.aliothliu.rbac.application.command.NewRoleCommand;
import io.github.aliothliu.rbac.domain.menu.MenuId;
import io.github.aliothliu.rbac.domain.page.ElementId;
import io.github.aliothliu.rbac.domain.role.Role;
import io.github.aliothliu.rbac.domain.role.RoleCode;
import io.github.aliothliu.rbac.domain.role.RoleRepository;
import io.github.aliothliu.rbac.infrastructure.errors.NonMatchEntityException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.stream.Collectors;

/**
 * @author Alioth Liu
 **/
@Service
@RequiredArgsConstructor
@Validated
public class RoleApplicationService {

    private final static String NOT_FOUND_ROLE_MESSAGE = "操作失败：未查询到匹配的角色数据";
    private final RoleRepository repository;

    public RoleCode newRole(@Valid NewRoleCommand command) {
        Role role = Role.builder().code(new RoleCode(command.getCode())).name(command.getName()).description(command.getDescription()).build();
        this.repository.save(role);
        return role.getCode();
    }

    public RoleCode changeRole(@NotNull RoleCode code, @Valid ChangeRoleCommand command) {
        Role role = this.repository.findByCode(code).orElseThrow(() -> new NonMatchEntityException(NOT_FOUND_ROLE_MESSAGE));
        role.changeName(command.getName());
        role.changeDescription(command.getDescription());
        role.changeType(command.getType());
        this.repository.save(role);
        return role.getCode();
    }

    @Transactional(rollbackFor = Exception.class)
    public void grant(@NotNull RoleCode code, @Valid GrantMenuCommand command) {
        Role role = this.repository.findByCode(code).orElseThrow(() -> new NonMatchEntityException(NOT_FOUND_ROLE_MESSAGE));
        role.grantMenus(command.getMenus().stream().map(MenuId::new).collect(Collectors.toSet()));
    }

    @Transactional(rollbackFor = Exception.class)
    public void grant(@NotNull RoleCode code, @Valid GrantElementCommand command) {
        Role role = this.repository.findByCode(code).orElseThrow(() -> new NonMatchEntityException(NOT_FOUND_ROLE_MESSAGE));

        role.grantElements(command.getElements().stream().map(ElementId::new).collect(Collectors.toSet()));
    }
}
