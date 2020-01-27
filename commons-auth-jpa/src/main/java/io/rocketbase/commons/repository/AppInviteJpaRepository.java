package io.rocketbase.commons.repository;

import io.rocketbase.commons.model.AppInviteJpaEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

public interface AppInviteJpaRepository extends PagingAndSortingRepository<AppInviteJpaEntity, String>, QueryByExampleExecutor<AppInviteJpaEntity>, JpaSpecificationExecutor<AppInviteJpaEntity> {
}
