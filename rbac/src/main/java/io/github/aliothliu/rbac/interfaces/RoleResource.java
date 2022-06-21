package io.github.aliothliu.rbac.interfaces;

import io.github.aliothliu.rbac.application.RoleApplicationService;
import io.github.aliothliu.rbac.application.RoleQueryService;
import io.github.aliothliu.rbac.application.command.ChangeRoleCommand;
import io.github.aliothliu.rbac.application.command.GrantElementCommand;
import io.github.aliothliu.rbac.application.command.GrantMenuCommand;
import io.github.aliothliu.rbac.application.command.NewRoleCommand;
import io.github.aliothliu.rbac.application.representation.RoleDetailRepresentation;
import io.github.aliothliu.rbac.application.representation.RoleRepresentation;
import io.github.aliothliu.rbac.domain.role.RoleCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/#{rbacProperties.web.prefix}/roles")
@Tag(name = "角色管理", description = "系统权限")
@RequiredArgsConstructor
public class RoleResource {

    private final RoleApplicationService applicationService;

    private final RoleQueryService queryService;

    @GetMapping("/{code}")
    @Operation(summary = "查询角色详细信息")
    public ResponseEntity<RoleDetailRepresentation> roles(@PathVariable String code) {
        return this.queryService.getOne(new RoleCode(code)).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "查询角色列表")
    public ResponseEntity<org.springframework.data.domain.Page<RoleRepresentation>> roles(@RequestParam(required = false) @Parameter(name = "角色名称，模糊匹配") String name,
                                                                                          Pageable pageable) {
        return ResponseEntity.ok(this.queryService.paging(name, pageable));
    }

    @PostMapping
    @Operation(summary = "新建角色")
    public ResponseEntity<String> create(@RequestBody @Valid NewRoleCommand command) {
        RoleCode roleCode = this.applicationService.newRole(command);
        return ResponseEntity.ok(roleCode.getCode());
    }

    @PutMapping("/{code}")
    @Operation(summary = "修改角色")
    public ResponseEntity<Void> create(@PathVariable String code, @RequestBody @Valid ChangeRoleCommand command) {
        this.applicationService.changeRole(new RoleCode(code), command);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{code}/menus")
    @Operation(summary = "设置角色菜单关联")
    public ResponseEntity<Void> assignRoleForMenus(@PathVariable String code, @RequestBody @Valid GrantMenuCommand request) {
        this.applicationService.grant(new RoleCode(code), request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{code}/elements")
    @Operation(summary = "设置角色页面元素关联")
    public ResponseEntity<Void> assignRoleForPages(@PathVariable String code, @RequestBody @Valid GrantElementCommand request) {
        this.applicationService.grant(new RoleCode(code), request);
        return ResponseEntity.ok().build();
    }
}