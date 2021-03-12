package io.rocketbase.commons.converter;

import io.rocketbase.commons.dto.appteam.AppTeamRead;
import io.rocketbase.commons.model.AppTeamEntity;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AppTeamConverter {

    public <T extends AppTeamEntity> AppTeamRead fromEntity(T entity) {
        if (entity == null) {
            return null;
        }
        return AppTeamRead.builder()
                .id(entity.getId())
                .name(entity.getName())
                .systemRefId(entity.getSystemRefId())
                .description(entity.getDescription())
                .personal(entity.isPersonal())
                .created(entity.getCreated())
                .keyValues(KeyValueConverter.filterInvisibleKeys(entity.getKeyValues()))
                .created(entity.getCreated())
                .build();
    }

    public <T extends AppTeamEntity> List<AppTeamRead> fromEntities(Collection<T> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(e -> fromEntity(e))
                .collect(Collectors.toList());
    }
}
