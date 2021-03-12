package io.rocketbase.commons.converter;

import io.rocketbase.commons.dto.appinvite.AppInviteRead;
import io.rocketbase.commons.model.AppInviteEntity;

import java.util.List;
import java.util.stream.Collectors;

public interface AppInviteConverter<E extends AppInviteEntity> {

    AppInviteRead fromEntity(E entity);

    default List<AppInviteRead> fromEntities(List<E> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(e -> fromEntity(e))
                .collect(Collectors.toList());
    }
}
