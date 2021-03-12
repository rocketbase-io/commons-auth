package io.rocketbase.commons.converter;

import io.rocketbase.commons.dto.appclient.AppClientRead;
import io.rocketbase.commons.model.AppClientEntity;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public interface AppClientConverter<E extends AppClientEntity> {

    AppClientRead fromEntity(E entity);

    default List<AppClientRead> fromEntities(Collection<E> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(e -> fromEntity(e))
                .collect(Collectors.toList());
    }

}
