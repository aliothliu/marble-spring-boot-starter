package io.github.aliothliu.rbac.application.conversion;

import io.github.aliothliu.rbac.application.representation.PageRepresentation;
import io.github.aliothliu.rbac.domain.page.Element;
import io.github.aliothliu.rbac.domain.page.Page;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * @author Alioth Liu
 **/
@Mapper
public interface PageConversion {
    PageConversion INSTANCE = Mappers.getMapper(PageConversion.class);

    @Mappings({
            @Mapping(source = "pageId.id", target = "id"),
            @Mapping(source = "path.path", target = "path"),
            @Mapping(source = "path.target", target = "target"),
    })
    PageRepresentation from(Page page);

    @Mappings({
            @Mapping(source = "id.id", target = "id"),
            @Mapping(source = "api.method", target = "method"),
            @Mapping(source = "api.url", target = "url"),
    })
    PageRepresentation.Element from(Element element);
}
