package io.rocketbase.commons.converter;

import io.rocketbase.commons.model.AppCapabilityJpaEntity;
import io.rocketbase.commons.model.AppUserJpaEntity;
import io.rocketbase.commons.model.SimpleAppUser;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class UserJpaConverter {

    public static Optional<AppUserEntity> convertOptional(Optional<AppUserJpaEntity> optional) {
        if (optional == null || !optional.isPresent()) {
            return Optional.empty();
        }
        return Optional.ofNullable(convert(optional.get()));
    }

    public static AppUserEntity convert(AppUserJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return SimpleAppUser.builder()
                .id(entity.getId())
                .systemRefId(entity.getSystemRefId())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .email(entity.getEmail())
                .capabilities(entity.getCapabilities() != null ? entity.getCapabilities().stream().map(AppCapabilityJpaEntity::getId).collect(Collectors.toSet()) :  new HashSet<>())
                .keyValues(entity.getKeyValues())
                .groups(GroupJpaConverter.convertSet(entity.getGroups()))
                .activeTeam(null)
                .enabled(entity.isEnabled())
                .created(entity.getCreated())
                .lastLogin(entity.getLastLogin())
                .lastTokenInvalidation(entity.getLastTokenInvalidation())
                .profile(entity.getProfile())
                .setting(entity.getSetting())
                .build();
    }



    public static AppUserJpaEntity convert(AppUserEntity dto) {
        if (dto == null) {
            return null;
        }
        return AppUserJpaEntity.builder()
                .id(dto.getId())
                .systemRefId(dto.getSystemRefId())
                .username(dto.getUsername())
                .password(dto.getPassword())
                .email(dto.getEmail())
                //.capabilities()
                .keyValues(dto.getKeyValues())
               // .groups(GroupJpaConverter.convertSet(dto.getGroups()))
                .activeTeam(null)
                .enabled(dto.isEnabled())
                .created(dto.getCreated())
                .lastLogin(dto.getLastLogin())
                .lastTokenInvalidation(dto.getLastTokenInvalidation())
                // .profile(dto.getProfile())
                // .setting(dto.getSetting())
                .build();
    }

    public static List<AppUserEntity> convertList(Collection<AppUserJpaEntity> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream().map(UserJpaConverter::convert).collect(Collectors.toList());
    }
}
