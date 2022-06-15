package io.github.aliothliu.marble.rbac.domain;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PageRepository {

    boolean existsById(PageId pageId);

    Optional<Page> ofId(PageId pageId);
}
