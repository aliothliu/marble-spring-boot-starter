package io.github.aliothliu.rbac.domain.role;

import io.github.aliothliu.rbac.RbacRegistry;
import io.github.aliothliu.rbac.domain.AssertionConcern;
import io.github.aliothliu.rbac.domain.Identity;
import io.github.aliothliu.rbac.domain.menu.Menu;
import io.github.aliothliu.rbac.domain.menu.MenuId;
import io.github.aliothliu.rbac.domain.page.Element;
import io.github.aliothliu.rbac.domain.page.ElementId;
import io.github.aliothliu.rbac.domain.page.ElementRepository;
import io.github.aliothliu.rbac.domain.page.PageId;
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

    public List<List<String>> newPoliciesFromMenu(List<Menu> menus) {
        List<List<String>> policies = this.newPolicies(menus.stream().map(Menu::getMenuId).collect(Collectors.toList()));
        List<PageId> pageIds = menus
                .stream()
                .map(Menu::getPageId)
                .filter(pageId -> Objects.nonNull(pageId) && Objects.nonNull(pageId.getId()))
                .collect(Collectors.toList());
        policies.addAll(this.newPolicies(new ArrayList<>(pageIds)));

        return policies;
    }

    public List<List<String>> newPoliciesFromElement(List<Element> elements) {
        return elements.stream().map(element -> Arrays.asList(
                this.code.getCode(),
                element.getId().getId(),
                Element.class.getSimpleName(),
                element.getPageId().getId())).collect(Collectors.toList());
    }

    public Set<MenuId> loadMenus() {
        this.enforcer().loadPolicy();
        return this.granted(Menu.class)
                .stream()
                .map(rule -> rule.get(1))
                .map(MenuId::new)
                .collect(Collectors.toSet());
    }

    public Set<PageId> loadPages() {
        this.enforcer().loadPolicy();
        return this.granted(Page.class)
                .stream()
                .map(rule -> rule.get(1))
                .map(PageId::new)
                .collect(Collectors.toSet());
    }

    public Set<String> loadElements() {
        this.enforcer().loadPolicy();
        return this.granted(Element.class)
                .stream()
                .map(rule -> rule.get(1))
                .collect(Collectors.toSet());
    }

    public boolean hasMenu(MenuId menuId) {
        if (Objects.isNull(menuId)) {
            return false;
        }
        this.enforcer().loadPolicy();
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

    public List<List<String>> newPolicies(List<Identity> identities) {
        if (Objects.isNull(identities)) {
            return new ArrayList<>();
        }

        return identities.stream().map(identity -> Arrays.asList(this.code.getCode(), identity.getId(), identity.target().getSimpleName())).collect(Collectors.toList());
    }

    public List<List<String>> granted(Class<?> clazz) {
        if (Objects.isNull(clazz)) {
            return new ArrayList<>();
        }
        return this.enforcer().getFilteredPolicy(0, this.code.getCode(), null, clazz.getSimpleName());
    }

    private Enforcer enforcer() {
        return RbacRegistry.enforcer();
    }

    private ElementRepository elementRepository() {
        return RbacRegistry.elementRepository();
    }
}
