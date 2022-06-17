package io.github.aliothliu.rbac.domain.menu;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuPathRepository extends CrudRepository<MenuPath, String>, JpaSpecificationExecutor<MenuPath> {

    /**
     * 查询节点至根节点的路径
     *
     * @param descendant 节点ID
     * @return 节点的路径对象
     */
    List<MenuPath> findByDescendant(MenuId descendant);

    /**
     * 删除节点至根节点的路径
     *
     * @param descendant 节点ID
     */
    void deleteByDescendant(MenuId descendant);

    /**
     * 删除节点至根节点的路径
     *
     * @param descendants 节点ID
     */
    void deleteByDescendantIn(List<MenuId> descendants);

    /**
     * 删除作为其他节点的父节点的节点
     *
     * @param ancestors 节点ID
     */
    void deleteByAncestorIn(List<MenuId> ancestors);

    List<MenuPath> findByAncestor(MenuId ancestor);

    /**
     * 查询节点的第N级的子节点
     *
     * @param ancestor 节点ID
     * @param distance 子节点距离父级的距离(0表示自己，1表示下一级子节点)
     * @return 子节点路径
     */
    List<MenuPath> findByAncestorAndDistance(MenuId ancestor, int distance);

    /**
     * 查询节点的第N级的父节点
     *
     * @param descendant 节点ID
     * @param distance   节点距离父级的距离(0表示自己，1表示直属父节点)
     * @return 路径
     */
    Optional<MenuPath> findByDescendantAndDistance(MenuId descendant, int distance);

    /**
     * 查询祖先节点与后代节点的距离
     *
     * @param ancestor   祖先节点
     * @param descendant 后代节点
     * @return 路径
     */
    Optional<MenuPath> findByAncestorAndDescendant(MenuId ancestor, MenuId descendant);

}
