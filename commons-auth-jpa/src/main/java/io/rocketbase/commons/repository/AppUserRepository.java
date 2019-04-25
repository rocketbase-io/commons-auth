package io.rocketbase.commons.repository;

import io.rocketbase.commons.model.AppUserEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.util.Optional;

public interface AppUserRepository extends PagingAndSortingRepository<AppUserEntity, String>, QueryByExampleExecutor<AppUserEntity> {

    Optional<AppUserEntity> findByUsername(String username);

    Optional<AppUserEntity> findByEmail(String email);
}
