package io.rocketbase.commons.converter;

import io.rocketbase.commons.dto.appgroup.AppGroupRead;
import io.rocketbase.commons.model.AppGroupJpaEntity;

import java.util.Set;
import java.util.stream.Collectors;

public final class GroupJpaConverter {

    public static Set<AppGroupRead> convertSet(Set<AppGroupJpaEntity> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(e -> new AppGroupRead(e.getId(), e.getSystemRefId(), e.getName(), e.getNamePath(), e.getNamePath(), e.isWithChildren(), e.getParent().getId(), null, null, null))
                .collect(Collectors.toSet());
    }
}
