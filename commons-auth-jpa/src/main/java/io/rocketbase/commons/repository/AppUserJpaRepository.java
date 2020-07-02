package io.rocketbase.commons.repository;

import io.rocketbase.commons.model.AppUserJpaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.util.Optional;

public interface AppUserJpaRepository extends PagingAndSortingRepository<AppUserJpaEntity, String>, QueryByExampleExecutor<AppUserJpaEntity>, JpaSpecificationExecutor<AppUserJpaEntity> {

    @EntityGraph(attributePaths = {"roles", "keyValueMap"})
    Optional<AppUserJpaEntity> findByUsername(String username);

    @EntityGraph(attributePaths = {"roles", "keyValueMap"})
    Optional<AppUserJpaEntity> findByEmail(String email);
}
