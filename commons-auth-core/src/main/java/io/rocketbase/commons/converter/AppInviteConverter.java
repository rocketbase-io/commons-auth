package io.rocketbase.commons.converter;

import io.rocketbase.commons.dto.ErrorResponse;
import io.rocketbase.commons.dto.appinvite.AppInviteRead;
import io.rocketbase.commons.dto.appinvite.InviteRequest;
import io.rocketbase.commons.exception.BadRequestException;
import io.rocketbase.commons.model.AppInviteEntity;
import io.rocketbase.commons.util.RolesAuthoritiesConverter;

import java.util.List;
import java.util.stream.Collectors;

import static io.rocketbase.commons.converter.AppUserConverter.filterInvisibleKeys;

public class AppInviteConverter {

    public AppInviteEntity updateEntity(AppInviteEntity entity, InviteRequest request) {
        entity.setInvitor(request.getInvitor());
        entity.setMessage(request.getMessage());
        entity.setFirstName(request.getFirstName());
        entity.setLastName(request.getLastName());
        entity.setEmail(request.getEmail());
        entity.setRoles(request.getRoles());
        try {
            if (request.getKeyValues() != null) {
                request.getKeyValues().forEach((k, v) -> {
                    entity.addKeyValue(k, v);
                });
            }
        } catch (IllegalStateException e) {
            throw new BadRequestException(new ErrorResponse("invalid key-value pair")
                    .addField("keyValues", e.getMessage()));
        }
        return entity;
    }

    public AppInviteRead fromEntity(AppInviteEntity entity) {
        if (entity == null) {
            return null;
        }
        return AppInviteRead.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .invitor(entity.getInvitor())
                .message(entity.getMessage())
                .email(entity.getEmail())
                .roles(RolesAuthoritiesConverter.convertRoles(entity.getRoles()))
                .keyValues(filterInvisibleKeys(entity.getKeyValues()))
                .created(entity.getCreated())
                .expiration(entity.getExpiration())
                .build();
    }

    public List<AppInviteRead> fromEntities(List<AppInviteEntity> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(e -> fromEntity(e))
                .collect(Collectors.toList());
    }
}
