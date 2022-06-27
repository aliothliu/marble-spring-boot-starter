package io.github.aliothliu.marble.domain.page;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PageRepository extends PagingAndSortingRepository<Page, PageId>, JpaSpecificationExecutor<Page> {
    @Override
    List<Page> findAllById(Iterable<PageId> ids);
}
