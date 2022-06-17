package io.github.aliothliu.rbac.domain.menu;

import io.github.aliothliu.rbac.RbacProperties;
import io.github.aliothliu.rbac.domain.Sort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 菜单排序领域服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MenuSortService {

    private final JdbcTemplate jdbcTemplate;

    private final RbacProperties rbacProperties;

    public Sort last(MenuId parentId) {
        /*
         * 非线程安全
         */
        String SQL;
        if (Objects.isNull(parentId)) {
            SQL = "SELECT menu.sort FROM `" + this.rbacProperties.getJpa().getMenuTableName() + "` menu RIGHT JOIN(SELECT descendant FROM " + this.rbacProperties.getJpa().getMenuPathTableName() + " GROUP BY descendant HAVING count(descendant) = 1) menu_path ON menu.id = menu_path.descendant ORDER BY menu.sort DESC LIMIT 1";
        } else {
            SQL = "SELECT menu.sort FROM `" + this.rbacProperties.getJpa().getMenuTableName() + "` menu RIGHT JOIN " + this.rbacProperties.getJpa().getMenuPathTableName() + " menu_path ON menu.id = menu_path.descendant WHERE menu_path.ancestor = '" + parentId.getId() + "' AND distance = 1 ORDER BY menu.sort DESC LIMIT 1";
        }

        Sort sort = Sort.one();
        try {
            Integer max = this.jdbcTemplate.queryForObject(SQL, Integer.class);
            if (Objects.isNull(max)) {
                max = 0;
            }
            sort = new Sort(max);
            sort.increment();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
        }

        return sort;
    }

    public Sort move(MenuId menuId, int offset) {
        return Sort.one();
    }
}
