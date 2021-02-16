package io.rocketbase.commons.converter;

import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.model.AppUserToken;

import java.util.List;
import java.util.stream.Collectors;

public interface AppUserConverter<E extends AppUserEntity> {

    AppUserToken toToken(E entity);

    AppUserRead toRead(E entity);

        /*
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
                .roles(RolesAuthoritiesConverter.convertRoles(entity.getRoles()))
                .keyValues(filterInvisibleKeys(entity.getKeyValues()))
                .enabled(entity.isEnabled())
                .created(entity.getCreated())
                .lastLogin(entity.getLastLogin())
                .build();
      */

    default List<AppUserRead> toRead(List<E> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(e -> toRead(e))
                .collect(Collectors.toList());
    }

}
