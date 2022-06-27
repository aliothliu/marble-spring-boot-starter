package io.github.aliothliu.marble.application.conversion;

import io.github.aliothliu.marble.application.representation.RoleDetailRepresentation;
import io.github.aliothliu.marble.application.representation.RoleRepresentation;
import io.github.aliothliu.marble.domain.role.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * @author Alioth Liu
 **/
@Mapper
public interface RoleConversion {

    RoleConversion INSTANCE = Mappers.getMapper(RoleConversion.class);

    @Mappings({
            @Mapping(target = "code", source = "code.code"),
            @Mapping(target = "statusDesc", source = "status.description"),
            @Mapping(target = "typeDesc", source = "type.description")
    })
    RoleRepresentation from(Role role);

    @Mappings({
            @Mapping(target = "code", source = "code.code"),
            @Mapping(target = "statusDesc", source = "status.description"),
            @Mapping(target = "typeDesc", source = "type.description")
    })
    RoleDetailRepresentation detailFrom(Role role);
}
