package io.github.aliothliu.marble.domain.page;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Alioth Liu
 **/
@Repository
public interface ElementRepository extends PagingAndSortingRepository<Element, ElementId>, JpaSpecificationExecutor<Element> {

    @Override
    List<Element> findAllById(Iterable<ElementId> ids);
}
