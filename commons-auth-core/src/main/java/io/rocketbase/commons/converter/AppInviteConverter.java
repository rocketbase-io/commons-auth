package io.rocketbase.commons.converter;

import io.rocketbase.commons.dto.appinvite.AppInviteRead;
import io.rocketbase.commons.model.AppInviteEntity;

import java.util.List;
import java.util.stream.Collectors;

public interface AppInviteConverter<E extends AppInviteEntity> {

    AppInviteRead fromEntity(E entity);
    /*
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
                .teamInvite(entity.getTeamInvite())
                //.capabilities(entity.getCapabilities() != null ? appCapabilityService.lookup(entity.getCapabilities()) : new HashSet<>())
                .keyValues(filterInvisibleKeys(entity.getKeyValues()))
                // .groups(entity.getGroups() != null ? entity.getGroups().stream().map(AppGroupRead::toShort).collect(Collectors.toSet()) : new HashSet<>())
                .created(entity.getCreated())
                .expiration(entity.getExpiration())
                .build();
     */

    default List<AppInviteRead> fromEntities(List<E> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(e -> fromEntity(e))
                .collect(Collectors.toList());
    }
}
