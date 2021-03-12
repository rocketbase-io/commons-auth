package io.rocketbase.commons.converter;

import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.model.AppUserToken;

import java.util.List;
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

}
