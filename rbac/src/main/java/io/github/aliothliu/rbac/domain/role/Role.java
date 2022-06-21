package io.github.aliothliu.rbac.domain.role;

import io.github.aliothliu.rbac.RbacRegistry;
import io.github.aliothliu.rbac.domain.AssertionConcern;
import io.github.aliothliu.rbac.domain.menu.Menu;
import io.github.aliothliu.rbac.domain.menu.MenuId;
import io.github.aliothliu.rbac.domain.menu.MenuRepository;
import io.github.aliothliu.rbac.domain.page.Element;
import io.github.aliothliu.rbac.domain.page.ElementId;
import io.github.aliothliu.rbac.domain.page.ElementRepository;
import io.github.aliothliu.rbac.domain.page.PageId;
import io.github.aliothliu.rbac.infrastructure.errors.RoleGrantException;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.casbin.jcasbin.main.Enforcer;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.domain.Page;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
@Entity
@Table(name = "#{rbacProperties.jpa.roleTableName}")
@Getter
@FieldNameConstants
public class Role extends AssertionConcern implements Serializable {

    @Id
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "uuid")
    private String id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "code", column = @Column(name = "code", length = 40))
    })
    @NonNull
    private RoleCode code;

    @Column(name = "type", length = 16)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Type type = Type.Super;

    @Column(name = "name", length = 64)
    @NonNull
    private String name;

    @Column(name = "status", length = 32)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.ENABLE;

    private String description;

    public void validate() {
        this.assertArgumentFalse(RbacRegistry.roleRepository().existsByCode(this.code), "不允许重复的角色编码[" + code.getCode() + "]");
    }

    public void changeName(String name) {
        Assert.notNull(name, "角色名称不能为空");
        this.name = name;
    }

    public void changeDescription(String description) {
        this.description = description;
    }

    public void changeStatus(Status status) {
        this.assertArgumentNotNull(name, "角色状态不能为空");
        this.status = status;
    }

    public void changeType(Type type) {
        this.assertArgumentNotNull(name, "角色类型不能为空");
        this.type = type;
    }

    public boolean isSuper() {
        return Type.Super.equals(this.type);
    }

    public void grantMenus(Set<MenuId> menus) {
        // 移除现有关联菜单
        this.enforcer().removePolicies(this.grantedMenuPolicies());
        this.enforcer().removePolicies(this.grantedPagePolicies());
        this.enforcer().loadPolicy();
        // 授权菜单策略
        List<Menu> storedMenus = this.menuRepository().findAllById(menus);
        if (storedMenus.isEmpty()) {
            throw new RoleGrantException("角色权限分配失败，不允许分配空菜单");
        }
        List<List<String>> policies = new ArrayList<>(this.convertMenu2Policy(this.code, storedMenus));
        // 授权页面策略
        List<PageId> pageIds = storedMenus
                .stream()
                .map(Menu::getPageId)
                .filter(pageId -> Objects.nonNull(pageId) && Objects.nonNull(pageId.getId()))
                .collect(Collectors.toList());
        policies.addAll(this.convertPage2Policy(this.code, pageIds));

        this.enforcer().addPolicies(policies);
    }

    public void grantElements(Set<ElementId> elements) {
        // 移除现有关联元素
        this.enforcer().removePolicies(this.grantedElementPolicies());
        this.enforcer().loadPolicy();

        // 授权元素策略
        List<Element> storedElements = this.elementRepository().findAllById(elements);

        List<List<String>> policies = storedElements.stream().map(element -> Arrays.asList(
                this.code.getCode(),
                element.getId().getId(),
                Element.class.getSimpleName(),
                element.getPageId().getId())).collect(Collectors.toList());

        this.enforcer().addPolicies(policies);
    }

    private List<List<String>> grantedMenuPolicies() {
        return this.enforcer().getFilteredPolicy(0, this.code.getCode(), null, Menu.class.getSimpleName());
    }

    public Set<MenuId> loadMenus() {
        this.enforcer().loadPolicy();
        return this.grantedMenuPolicies()
                .stream()
                .map(rule -> rule.get(1))
                .map(MenuId::new)
                .collect(Collectors.toSet());
    }

    private List<List<String>> grantedPagePolicies() {
        return this.enforcer().getFilteredPolicy(0, this.code.getCode(), null, Page.class.getSimpleName());
    }

    public Set<PageId> loadPages() {
        this.enforcer().loadPolicy();
        return this.grantedPagePolicies()
                .stream()
                .map(rule -> rule.get(1))
                .map(PageId::new)
                .collect(Collectors.toSet());
    }

    private List<List<String>> grantedElementPolicies() {
        return this.enforcer().getFilteredPolicy(0, this.code.getCode(), null, Element.class.getSimpleName());
    }

    public Set<String> loadElements() {
        this.enforcer().loadPolicy();
        return this.grantedElementPolicies()
                .stream()
                .map(rule -> rule.get(1))
                .collect(Collectors.toSet());
    }

    public boolean hasMenu(MenuId menuId) {
        if (Objects.isNull(menuId)) {
            return false;
        }
        this.enforcer().loadPolicy();
        List<List<String>> policies = this.enforcer().getPolicy();
        return this.enforcer().hasPolicy(Arrays.asList(this.code.getCode(), menuId.getId(), Menu.class.getSimpleName()));
    }

    public boolean hasPage(PageId pageId) {
        if (Objects.isNull(pageId)) {
            return false;
        }
        this.enforcer().loadPolicy();
        return this.enforcer().hasPolicy(Arrays.asList(this.code.getCode(), pageId.getId(), Page.class.getSimpleName()));
    }

    public boolean hasElement(ElementId elementId) {
        if (Objects.isNull(elementId) || Objects.isNull(elementId.getId())) {
            return false;
        }
        return this.elementRepository().findById(elementId).map(element -> {
            this.enforcer().loadPolicy();
            if (Objects.isNull(element.getPageId())) {
                return false;
            }
            return this.enforcer().hasPolicy(Arrays.asList(this.code.getCode(), elementId.getId(), Element.class.getSimpleName(), element.getPageId().getId()));
        }).orElse(false);

    }

    private List<List<String>> convertMenu2Policy(RoleCode code, List<Menu> menus) {
        return menus.stream().map(menu -> Arrays.asList(code.getCode(), menu.getMenuId().getId(), Menu.class.getSimpleName())).collect(Collectors.toList());
    }

    private List<List<String>> convertPage2Policy(RoleCode code, List<PageId> pageIds) {
        return pageIds.stream().map(pageId -> Arrays.asList(code.getCode(), pageId.getId(), Page.class.getSimpleName())).collect(Collectors.toList());
    }

    private Enforcer enforcer() {
        return RbacRegistry.enforcer();
    }

    private MenuRepository menuRepository() {
        return RbacRegistry.menuRepository();
    }

    private ElementRepository elementRepository() {
        return RbacRegistry.elementRepository();
    }
}
