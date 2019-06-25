package io.rocketbase.commons.converter;

import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.model.AppUserEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AppUserConverter {

    public static Map<String, String> filterInvisibleKeys(Map<String, String> keyValues) {
        if (keyValues == null) {
            return null;
        }
        Map<String, String> map = new HashMap<>();
        keyValues.entrySet().stream()
                .filter(e -> !e.getKey().startsWith("_"))
                .forEach(e -> map.put(e.getKey(), e.getValue()));
        return map;
    }

    public AppUserRead fromEntity(AppUserEntity entity) {
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

    public List<AppUserRead> fromEntities(List<AppUserEntity> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(e -> fromEntity(e))
                .collect(Collectors.toList());
    }

}
