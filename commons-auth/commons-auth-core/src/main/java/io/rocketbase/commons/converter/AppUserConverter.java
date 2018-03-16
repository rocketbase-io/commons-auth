package io.rocketbase.commons.converter;

import io.rocketbase.commons.config.AuthConfiguration;
import io.rocketbase.commons.dto.AppUserRead;
import io.rocketbase.commons.dto.RegistrationRequest;
import io.rocketbase.commons.model.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = "spring")
public abstract class AppUserConverter {

    @Resource
    private AuthConfiguration authConfiguration;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Mappings({})
    public abstract AppUserRead fromEntity(AppUser entity);

    public abstract List<AppUserRead> fromEntities(List<AppUser> entities);

    public AppUser createNew(AppUser entity, RegistrationRequest registration) {
        entity.setId(UUID.randomUUID().toString());
        entity.setUsername(registration.getUsername());
        entity.setFirstName(registration.getFirstName());
        entity.setLastName(registration.getLastName());
        entity.setEmail(registration.getEmail());
        entity.setPassword(passwordEncoder.encode(registration.getPassword()));
        entity.setEnabled(!authConfiguration.getRegistration().isEmailValidation());
        entity.setRoles(Arrays.asList(authConfiguration.getRegistration().getRole()));
        return entity;
    }

}
