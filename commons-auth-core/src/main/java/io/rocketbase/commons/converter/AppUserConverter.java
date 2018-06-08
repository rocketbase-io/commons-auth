package io.rocketbase.commons.converter;

import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.model.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = "spring")
public abstract class AppUserConverter {

    public AppUserRead fromEntity(AppUser entity) {
        if (entity == null) {
            return null;
        }
        return AppUserRead.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .avatar(entity.getAvatar())
                .roles(entity.getRoles())
                .keyValues(filterInvisibleKeys(entity.getKeyValues()))
                .enabled(entity.isEnabled())
                .created(entity.getCreated())
                .lastLogin(entity.getLastLogin())
                .build();
    }

    private Map<String, String> filterInvisibleKeys(Map<String, String> keyValues) {
        if (keyValues == null) {
            return null;
        }
        Map<String, String> map = new HashMap<>();
        keyValues.entrySet().stream()
                .filter(e -> !e.getKey().startsWith("_"))
                .forEach(e -> map.put(e.getKey(), e.getValue()));
        return map;
    }

    public abstract List<AppUserRead> fromEntities(List<AppUser> entities);

}
