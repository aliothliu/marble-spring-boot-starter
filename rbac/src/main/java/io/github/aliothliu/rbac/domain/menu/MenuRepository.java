package io.github.aliothliu.rbac.domain.menu;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends CrudRepository<Menu, MenuId>, JpaSpecificationExecutor<Menu> {

    @Override
    List<Menu> findAllById(Iterable<MenuId> ids);
}
