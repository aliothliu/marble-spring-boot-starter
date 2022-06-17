package io.github.aliothliu.rbac.interfaces;

import io.github.aliothliu.rbac.application.RbacMenuService;
import io.github.aliothliu.rbac.application.command.ChangeMenuCommand;
import io.github.aliothliu.rbac.application.command.NewMenuCommand;
import io.github.aliothliu.rbac.application.representation.MenuRepresentation;
import io.github.aliothliu.rbac.domain.menu.MenuId;
import io.github.aliothliu.rbac.domain.page.PageId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/#{rbacProperties.web.prefix}/menus")
@Tag(name = "菜单", description = "系统权限")
@RequiredArgsConstructor
public class MenuResource {

    private final RbacMenuService menuManager;

    @GetMapping("/forest")
    @Operation(summary = "查询菜单树")
    public ResponseEntity<List<MenuRepresentation>> forest() {
        return ResponseEntity.ok(this.menuManager.forest());
    }

    @GetMapping("/roots")
    @Operation(summary = "查询根菜单")
    public ResponseEntity<List<MenuRepresentation>> roots(@RequestParam(required = false) @Parameter(name = "菜单名称，模糊匹配") String name) {
        return ResponseEntity.ok(this.menuManager.roots(name));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询菜单详情")
    public ResponseEntity<MenuRepresentation> getOne(@PathVariable String id) {
        return this.menuManager.getOne(new MenuId(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "新建菜单")
    public ResponseEntity<String> newMenu(@RequestBody @Valid NewMenuCommand request) {
        PageId pageId = this.createPageId(request.getPageId());
        MenuId id = this.menuManager.append(request.getName(), pageId, request.getIcon(), MenuId.nullable(request.getParentId()));
        return ResponseEntity.ok(id.getId());
    }

    private PageId createPageId(String pageId) {
        if (Objects.isNull(pageId)) {
            return null;
        }

        return new PageId(pageId);
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改菜单")
    public ResponseEntity<Void> changeMenu(@PathVariable String id, @RequestBody @Valid ChangeMenuCommand request) {
        this.menuManager.update(new MenuId(id),
                request.getName(),
                this.createPageId(request.getPageId()),
                request.getIcon(),
                MenuId.nullable(request.getParentId()));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除菜单")
    public ResponseEntity<Void> remove(@PathVariable String id) {
        this.menuManager.remove(new MenuId(id));
        return ResponseEntity.ok().build();
    }
}