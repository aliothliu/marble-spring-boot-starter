package io.github.aliothliu.marble.application;

import io.github.aliothliu.marble.application.command.NewPageCommand;
import io.github.aliothliu.marble.application.conversion.PageConversion;
import io.github.aliothliu.marble.application.representation.PageRepresentation;
import io.github.aliothliu.marble.domain.page.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.util.Assert;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class RbacPageServiceImpl implements RbacPageService {

    private final PageRepository repository;

    @Override
    public org.springframework.data.domain.Page<PageRepresentation> paging(String name, Pageable pageable) {
        return this.repository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            if (Objects.isNull(name)) {
                return criteriaQuery.getRestriction();
            }
            return criteriaQuery
                    .where(criteriaBuilder.like(root.get(Page.Fields.name), "%" + name + "%"))
                    .getRestriction();
        }, pageable).map(PageConversion.INSTANCE::from);
    }

    @Override
    public Optional<PageRepresentation> getOne(PageId id) {
        return this.getPage(id).map(PageConversion.INSTANCE::from);
    }

    @Override
    public PageId newPage(String name, String path, PathTarget target, List<NewPageCommand.Element> elements) {
        Set<Element> pageElements = elements.stream()
                .map(el -> Element.builder().id(ElementId.uuid()).name(el.getName()).readableName(el.getReadableName()).api(new Api(el.getMethod(), el.getUri())).build())
                .collect(Collectors.toSet());
        Page page = new Page(PageId.uuid(), name, new Path(path, target));
        page.changeElements(pageElements);
        this.repository.save(page);
        return page.getPageId();
    }

    @Override
    public void changePage(PageId id, List<NewPageCommand.Element> elements) {
        this.changePage(id, null, null, elements);
    }

    private Optional<Page> getPage(PageId id) {
        Assert.notNull(id, "查询参数ID不能为空");
        return this.repository.findById(id);
    }

    @Override
    public void changePage(PageId id, String name, String path, List<NewPageCommand.Element> elements) {
        Page page = this.getPage(id).orElseThrow(() -> new EntityNotFoundException("页面更新失败：未查询到匹配的页面数据"));
        if (Objects.nonNull(name)) {
            page.changeName(name);
        }
        if (Objects.nonNull(path)) {
            page.changePath(new Path(path));
        }
        page.changeElements(
                elements.stream()
                        .map(el -> Element.builder().name(el.getName()).readableName(el.getReadableName()).api(new Api(el.getMethod(), el.getUri())).build())
                        .collect(Collectors.toSet())
        );
        this.repository.save(page);
    }

    @Override
    public void remove(PageId id) {
        Assert.notNull(id, "删除参数ID不能为空");
        try {
            this.repository.deleteById(id);
        } catch (EmptyResultDataAccessException ex) {
            throw new EntityNotFoundException("页面删除失败，未查询到匹配的页面数据");
        }
    }
}
