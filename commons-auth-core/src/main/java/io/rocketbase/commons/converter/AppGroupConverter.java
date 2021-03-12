package io.rocketbase.commons.converter;

import io.rocketbase.commons.dto.appgroup.AppGroupRead;
import io.rocketbase.commons.model.AppGroupEntity;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public interface AppGroupConverter<E extends AppGroupEntity> {

    AppGroupRead fromEntity(E entity);

    default List<AppGroupRead> fromEntities(Collection<E> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(e -> fromEntity(e))
                .collect(Collectors.toList());
    }

}
