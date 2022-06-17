package io.github.aliothliu.rbac.interfaces;

import io.github.aliothliu.rbac.application.RbacPageService;
import io.github.aliothliu.rbac.application.command.ChangePageCommand;
import io.github.aliothliu.rbac.application.command.NewPageCommand;
import io.github.aliothliu.rbac.application.representation.PageRepresentation;
import io.github.aliothliu.rbac.domain.page.PageId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/#{rbacProperties.web.prefix}/pages")
@Tag(name = "页面管理", description = "系统权限")
@RequiredArgsConstructor
public class PageResource {

    private final RbacPageService pageManager;

    @GetMapping("/{id}/elements")
    @Operation(summary = "查询页面元素")
    public ResponseEntity<List<PageRepresentation.Element>> roles(@PathVariable(value = "id") String id) {
        return this.pageManager
                .getOne(new PageId(id))
                .map(PageRepresentation::getElements)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "查询页面列表")
    public ResponseEntity<Page<PageRepresentation>> pages(@RequestParam(required = false) @Parameter(name = "页面名称") String name, Pageable pageable) {
        return ResponseEntity.ok(this.pageManager.paging(name, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询页面详细信息")
    public ResponseEntity<PageRepresentation> getOne(@PathVariable(value = "id") String id) {
        return this.pageManager
                .getOne(new PageId(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "新建页面")
    public ResponseEntity<String> newPage(@RequestBody @Valid NewPageCommand newPage) {
        PageId page = this.pageManager.newPage(newPage.getName(), newPage.getPath(), newPage.getTarget(), newPage.getElements());
        return ResponseEntity.ok(page.getId());
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改页面")
    public ResponseEntity<Void> changePage(@PathVariable String id, @RequestBody @Valid ChangePageCommand changePage) {
        this.pageManager.changePage(new PageId(id), changePage.getName(), changePage.getPath(), changePage.getElements());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除页面")
    public ResponseEntity<Void> remove(@PathVariable String id) {
        this.pageManager.remove(new PageId(id));
        return ResponseEntity.ok().build();
    }
}
