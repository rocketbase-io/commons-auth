package io.rocketbase.commons.repository;

import io.rocketbase.commons.model.AppUserJpaEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.util.Optional;

public interface AppUserJpaRepository extends PagingAndSortingRepository<AppUserJpaEntity, String>, QueryByExampleExecutor<AppUserJpaEntity>, JpaSpecificationExecutor<AppUserJpaEntity> {

    @Query("select a from AppUserJpaEntity a left join fetch a.keyValueMap left join fetch a.roles where a.id = :id")
    Optional<AppUserJpaEntity> findById(@Param("id") String id);

    @Query("select a from AppUserJpaEntity a left join fetch a.keyValueMap left join fetch a.roles where a.username = :username")
    Optional<AppUserJpaEntity> findByUsername(@Param("username") String username);

    @Query("select a from AppUserJpaEntity a left join fetch a.keyValueMap left join fetch a.roles where a.email = :email")
    Optional<AppUserJpaEntity> findByEmail(@Param("email") String email);
}
