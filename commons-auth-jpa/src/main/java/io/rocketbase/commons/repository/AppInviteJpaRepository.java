package io.rocketbase.commons.repository;

import io.rocketbase.commons.model.AppInviteJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.util.Optional;

public interface AppInviteJpaRepository extends PagingAndSortingRepository<AppInviteJpaEntity, String>, QueryByExampleExecutor<AppInviteJpaEntity>, JpaSpecificationExecutor<AppInviteJpaEntity> {

    @Query("select a from AppInviteJpaEntity a left join fetch a.keyValueMap where a.id = :id")
    Optional<AppInviteJpaEntity> findById(@Param("id") String id);

    @Query(value = "select a from AppInviteJpaEntity a left join fetch a.keyValueMap",
            countQuery = "select count(a) from AppInviteJpaEntity a")
    Page<AppInviteJpaEntity> findAll(Pageable pageable);
}
