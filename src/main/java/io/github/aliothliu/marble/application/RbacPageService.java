package io.github.aliothliu.marble.application;

import io.github.aliothliu.marble.application.command.NewPageCommand;
import io.github.aliothliu.marble.application.representation.PageRepresentation;
import io.github.aliothliu.marble.domain.page.PageId;
import io.github.aliothliu.marble.domain.page.PathTarget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface RbacPageService {

    Page<PageRepresentation> paging(String name, Pageable pageable);

    Optional<PageRepresentation> getOne(PageId id);

    PageId newPage(String name, String path, PathTarget target, List<NewPageCommand.Element> elements);

    void changePage(PageId id, List<NewPageCommand.Element> elements);

    void changePage(PageId id, String name, String path, List<NewPageCommand.Element> elements);

    void remove(PageId id);
}
