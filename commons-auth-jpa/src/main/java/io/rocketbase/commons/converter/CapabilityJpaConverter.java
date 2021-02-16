package io.rocketbase.commons.converter;

import io.rocketbase.commons.model.AppCapabilityJpaEntity;

import java.util.Set;
import java.util.stream.Collectors;

public final class CapabilityJpaConverter {

    public static Set<String> convertSet(Set<AppCapabilityJpaEntity> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(AppCapabilityJpaEntity::getKeyPath)
                .collect(Collectors.toSet());
    }
}
