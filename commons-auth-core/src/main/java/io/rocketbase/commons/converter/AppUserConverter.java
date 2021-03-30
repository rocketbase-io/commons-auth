package io.rocketbase.commons.converter;

import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.model.AppGroupEntity;
import io.rocketbase.commons.model.AppTeamEntity;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.model.AppUserToken;

import java.util.*;
import java.util.stream.Collectors;

public interface AppUserConverter<E extends AppUserEntity> {

    AppUserToken toToken(E entity);

    AppUserRead fromEntity(E entity);

    default List<AppUserRead> fromEntities(List<E> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(e -> fromEntity(e))
                .collect(Collectors.toList());
    }

    default Set<Long> getCapabilityIdsOfGroups(Collection<? extends AppGroupEntity> entities) {
        Set<Long> result = new HashSet<>();
        if (entities != null) {
            for (AppGroupEntity e : entities) {
                if (e.getCapabilityIds() != null) {
                    result.addAll(e.getCapabilityIds());
                }
            }
        }
        return result;
    }

    default Map<String, String> resolveKeyValues(Collection<AppGroupEntity> groups, AppTeamEntity activeTeam, E entity) {
        Map<String, String> keyValues = new HashMap<>();
        if (groups != null) {
            for (AppGroupEntity g : groups.stream().sorted(Comparator.comparingLong(AppGroupEntity::getSortOrder)).collect(Collectors.toList())) {
                if (g.getKeyValues() != null) {
                    keyValues.putAll(entity.getKeyValues());
                }
            }
        }
        if (activeTeam != null) {
            if (activeTeam.getKeyValues() != null) {
                keyValues.putAll(activeTeam.getKeyValues());
            }
        }
        if (entity.getKeyValues() != null) {
            keyValues.putAll(entity.getKeyValues());
        }
        return keyValues;
    }
}
