package io.rocketbase.commons.service;

import io.rocketbase.commons.model.AppUser;

import java.util.Optional;

public interface ValidationUserLookupService {

    AppUser getByUsername(String username);

    Optional<AppUser> findByEmail(String email);
}
