package io.rocketbase.commons.repository;

import io.rocketbase.commons.model.AppUserJpaEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.util.Optional;

public interface AppUserJpaRepository extends PagingAndSortingRepository<AppUserJpaEntity, String>, QueryByExampleExecutor<AppUserJpaEntity> {

    Optional<AppUserJpaEntity> findByUsername(String username);

    Optional<AppUserJpaEntity> findByEmail(String email);
}
