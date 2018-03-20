package io.rocketbase.commons.repository;

import io.rocketbase.commons.model.AppUserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AppUserRepository extends MongoRepository<AppUserEntity, String> {

    Optional<AppUserEntity> findByUsername(String username);

    Optional<AppUserEntity> findByEmail(String email);
}
