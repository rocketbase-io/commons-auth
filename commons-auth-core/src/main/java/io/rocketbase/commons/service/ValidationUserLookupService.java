package io.rocketbase.commons.service;

import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.model.AppUserToken;

import java.util.Optional;

public interface ValidationUserLookupService {

    AppUserToken getByUsername(String username);

    Optional<AppUserEntity> findByEmail(String email);
}
