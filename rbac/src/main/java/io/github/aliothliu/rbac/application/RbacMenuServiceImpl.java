package io.github.aliothliu.rbac.application;

import io.github.aliothliu.rbac.RbacProperties;
import io.github.aliothliu.rbac.RbacRegistry;
import io.github.aliothliu.rbac.application.representation.MenuRepresentation;
import io.github.aliothliu.rbac.domain.menu.*;
import io.github.aliothliu.rbac.domain.page.*;
import io.github.aliothliu.rbac.infrastructure.errors.NonMatchEntityException;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class RbacMenuServiceImpl implements RbacMenuService {

    private final MenuRepository repository;

    private final MenuPathRepository pathRepository;

    private final JdbcTemplate jdbcTemplate;

    private final PageRepository pageRepository;

    private final MenuSortService sortService;

    private final RbacProperties rbacProperties;

    @Override
    public List<MenuRepresentation> roots(String nameLike) {
        return this.roots(nameLike, new HashSet<>());
    }

    private List<MenuRepresentation> roots(String nameLike, Set<MenuId> inMenus) {
        String SQL = "SELECT menu.id AS id, menu.icon AS icon, menu.name, menu.sort AS sort, menu.page_id AS page_id , page.path AS path, page.target AS target FROM " + this.rbacProperties.getJpa().getMenuTableName() + " menu RIGHT JOIN ( SELECT descendant FROM " + this.rbacProperties.getJpa().getMenuPathTableName() + " GROUP BY descendant HAVING count(descendant) = 1 ) menu_path ON menu.id = menu_path.descendant LEFT JOIN " + this.rbacProperties.getJpa().getPageTableName() + " page ON menu.page_id = page.id WHERE 1=1 ";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(this.jdbcTemplate);
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (StringUtils.hasText(nameLike)) {
            SQL += " AND menu.name like :nameLike";
            params.addValue("nameLike", "%" + nameLike + "%");
        }
        if (!inMenus.isEmpty()) {
            SQL += " AND menu.id IN (:inMenus)";
            params.addValue("inMenus", inMenus.stream().map(MenuId::getId).collect(Collectors.toList()));
        }
        SQL += " ORDER BY menu.sort ASC";
        return namedParameterJdbcTemplate.query(SQL, params, new BeanPropertyRowMapper<>(MenuRepresentation.class));
    }

    @Override
    public List<MenuRepresentation> forest() {
        return this.forest(new HashSet<>());
    }

    private List<MenuRepresentation> forest(Set<MenuId> inMenus) {
        // 查询roots
        List<MenuRepresentation> roots = this.roots(null, inMenus);
        // 查询全部后代，并且分组
        Map<String, Object> queryParams = new HashMap<>();
        String SQL = "SELECT menu.id AS id, menu.icon AS icon, menu.name AS name, menu.page_id AS page_id , page.path AS path, page.target AS target, menu_path.ancestor as ancestor FROM " + this.rbacProperties.getJpa().getMenuTableName() + " menu LEFT JOIN " + this.rbacProperties.getJpa().getMenuPathTableName() + " menu_path ON menu.id = menu_path.descendant LEFT JOIN " + this.rbacProperties.getJpa().getPageTableName() + " page ON menu.page_id = page.id WHERE menu_path.distance = 1 ";
        if (!inMenus.isEmpty()) {
            SQL += " AND menu_path.ancestor IN (:parentMenus) AND menu_path.descendant IN (:inMenus)";
            queryParams.put("parentMenus", roots.stream().map(MenuRepresentation::getId).collect(Collectors.toSet()));
            queryParams.put("inMenus", inMenus.stream().map(MenuId::getId).collect(Collectors.toList()));
        }
        SQL += " ORDER BY menu.sort ASC";
        Map<String, List<Map<String, Object>>> rawMap = new NamedParameterJdbcTemplate(jdbcTemplate).queryForList(SQL, queryParams)
                .stream()
                .collect(Collectors.groupingBy(map -> (String) map.get("ancestor")));

        Map<String, List<MenuRepresentation>> grouped = new HashMap<>(rawMap.size());
        rawMap.forEach((key, values) -> grouped.put(key, values.stream().map(this::from).collect(Collectors.toList())));

        List<MenuRepresentation> descendants = grouped.values().stream().flatMap(List::stream).collect(Collectors.toList());
        descendants.forEach(menu -> {
            List<MenuRepresentation> children = new ArrayList<>();
            if (grouped.containsKey(menu.getId())) {
                children = grouped.get(menu.getId());
            }
            menu.setChildren(children);
        });

        roots.forEach(root -> {
            if (grouped.containsKey(root.getId())) {
                root.setChildren(grouped.get(root.getId()));
            }
        });

        return roots;
    }

    @Override
    public Optional<MenuRepresentation> getOne(MenuId id) {
        Assert.notNull(id, "查询参数ID不能为空");
        return this.repository.findById(id).map(this::from);
    }

    @Override
    @Transactional
    public MenuId append(String name, PageId pageId, String icon, MenuId parentId) {
        if (Objects.nonNull(pageId)) {
            pageId.failFastValidate();
        }
        if (Objects.nonNull(parentId)) {
            parentId.failFastValidate();
        }
        Menu newMenu = Menu.builder()
                .name(name)
                .pageId(pageId)
                .icon(icon)
                .build();

        newMenu.setSort(this.sortService.last(parentId));

        this.pathRepository.saveAll(newMenu.paths(parentId));

        this.repository.save(newMenu);

        RbacRegistry.eventPublisher().publish(new MenuCreated(this, newMenu.getMenuId(), pageId));

        return newMenu.getMenuId();
    }

    @Override
    public void moveTo(MenuId menu, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(MenuId menuId, String name, PageId pageId, String icon, MenuId parentId) {
        Assert.notNull(menuId, "菜单ID不能为空");
        Menu menu = this.repository.findById(menuId).orElseThrow(() -> new NonMatchEntityException("更新失败：未查询到匹配的菜单数据"));

        menu.changeName(name);
        menu.changePageId(pageId);
        menu.changeIcon(icon);
        menu.moveDescendantsTo(parentId);

        this.repository.save(menu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(MenuId id) {
        Assert.notNull(id, "删除参数ID不能为空");
        Menu menu = this.repository.findById(id).orElseThrow(() -> new NonMatchEntityException("删除失败：未查询到匹配的菜单数据"));

        List<Menu> pendingRemove = menu.descendants();
        pendingRemove.add(menu);

        List<MenuId> pendingRemovePath = pendingRemove.stream().map(Menu::getMenuId).collect(Collectors.toList());
        this.pathRepository.deleteByDescendantIn(pendingRemovePath);
        this.pathRepository.deleteByAncestorIn(pendingRemovePath);

        this.repository.deleteAll(pendingRemove);
    }

    private MenuRepresentation from(Map<String, Object> raw) {
        MenuRepresentation menu = new MenuRepresentation();
        menu.setId((String) raw.get("id"));
        menu.setName((String) raw.get("name"));
        menu.setIcon((String) raw.get("icon"));
        menu.setPageId((String) raw.get("pageId"));
        menu.setPath((String) raw.get("path"));
        menu.setTarget(raw.containsKey("target") && Objects.nonNull(raw.get("target")) ? PathTarget.valueOf((String) raw.get("target")) : null);

        return menu;
    }

    private MenuRepresentation from(Menu menu) {
        MenuRepresentation representation = new MenuRepresentation();
        representation.setId(menu.getMenuId().getId());
        representation.setName(menu.getName());
        representation.setIcon(menu.getIcon());
        menu.getParentId().map(MenuId::getId).ifPresent(representation::setParentId);
        if (Objects.nonNull(menu.getPageId())) {
            representation.setPageId(menu.getPageId().getId());
            representation.setElements(
                    pageRepository
                            .findById(menu.getPageId())
                            .map(Page::getElements)
                            .map(elements -> elements
                                    .stream()
                                    .map(this::from)
                                    .collect(Collectors.toList())
                            )
                            .orElse(new ArrayList<>())
            );
        }
        representation.setPath(menu.getPagePath());
        representation.setTarget(menu.getPathTarget());

        return representation;
    }

    private MenuRepresentation.Element from(Element element) {
        return new MenuRepresentation.Element(element.getId().getId(), element.getName(), element.getReadableName());
    }

    protected List<MenuRepresentation> limitMenus(Set<MenuId> limited) {
        return this.forest(limited);
    }
}
