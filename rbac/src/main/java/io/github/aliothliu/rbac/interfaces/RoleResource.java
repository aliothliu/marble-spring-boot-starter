package io.github.aliothliu.rbac.interfaces;

import io.github.aliothliu.rbac.application.RbacRoleService;
import io.github.aliothliu.rbac.application.command.AssignMenusCommand;
import io.github.aliothliu.rbac.application.command.AssignPageElementsCommand;
import io.github.aliothliu.rbac.application.command.ChangeRoleCommand;
import io.github.aliothliu.rbac.application.command.NewRoleCommand;
import io.github.aliothliu.rbac.application.representation.RoleDetailRepresentation;
import io.github.aliothliu.rbac.application.representation.RoleRepresentation;
import io.github.aliothliu.rbac.domain.menu.MenuId;
import io.github.aliothliu.rbac.domain.page.PageId;
import io.github.aliothliu.rbac.domain.role.RoleCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/#{rbacProperties.web.prefix}/roles")
@Tag(name = "角色管理", description = "系统权限")
@RequiredArgsConstructor
public class RoleResource {

    private final RbacRoleService roleManager;

    @GetMapping("/{code}")
    @Operation(summary = "查询角色详细信息")
    public ResponseEntity<RoleDetailRepresentation> roles(@PathVariable String code) {
        return this.roleManager.getOne(new RoleCode(code)).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "查询角色列表")
    public ResponseEntity<org.springframework.data.domain.Page<RoleRepresentation>> roles(@RequestParam(required = false) @Parameter(name = "角色名称，模糊匹配") String name,
                                                                                          Pageable pageable) {
        return ResponseEntity.ok(this.roleManager.paging(name, pageable));
    }

    @PostMapping
    @Operation(summary = "新建角色")
    public ResponseEntity<String> create(@RequestBody @Valid NewRoleCommand request) {
        RoleCode roleCode = this.roleManager.newRole(new RoleCode(request.getCode()), request.getName(), request.getDescription());
        return ResponseEntity.ok(roleCode.getCode());
    }

    @PutMapping("/{code}")
    @Operation(summary = "修改角色")
    public ResponseEntity<Void> create(@PathVariable String code, @RequestBody @Valid ChangeRoleCommand request) {
        this.roleManager.changeRole(new RoleCode(code), request.getName(), request.getDescription());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{code}/menus")
    @Operation(summary = "设置角色菜单关联")
    public ResponseEntity<Void> assignRoleForMenus(@PathVariable String code, @RequestBody @Valid AssignMenusCommand request) {
        this.roleManager.grant(new RoleCode(code), request.getMenus().stream().map(MenuId::new).collect(Collectors.toSet()));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/page-elements")
    @Operation(summary = "设置角色页面元素关联")
    public ResponseEntity<Void> assignRoleForPages(@PathVariable String id, @RequestBody @Valid List<AssignPageElementsCommand> commands) {
        Map<PageId, Set<String>> params = new LinkedHashMap<>();
        commands.forEach(assignPageElementsCommand -> {
            params.put(new PageId(assignPageElementsCommand.getPageId()), assignPageElementsCommand.getElements());
        });
        this.roleManager.grant(new RoleCode(id), params);
        return ResponseEntity.ok().build();
    }
}