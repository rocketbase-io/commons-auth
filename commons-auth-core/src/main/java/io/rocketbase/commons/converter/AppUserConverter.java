package io.rocketbase.commons.converter;

import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.model.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = "spring")
public abstract class AppUserConverter {

    @Mappings({})
    public abstract AppUserRead fromEntity(AppUser entity);

    public abstract List<AppUserRead> fromEntities(List<AppUser> entities);

}
