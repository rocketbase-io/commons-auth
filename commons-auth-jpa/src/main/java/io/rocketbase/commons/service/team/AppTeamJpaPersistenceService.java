package io.rocketbase.commons.service.team;

import io.rocketbase.commons.dto.appgroup.QueryAppGroup;
import io.rocketbase.commons.model.AppTeamJpaEntity;
import io.rocketbase.commons.service.JpaQueryHelper;
import io.rocketbase.commons.util.Snowflake;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import java.util.Optional;

public class AppTeamJpaPersistenceService implements AppTeamPersistenceService<AppTeamJpaEntity>, JpaQueryHelper {

    private final EntityManager em;
    private final Snowflake snowflake;

    private final SimpleJpaRepository<AppTeamJpaEntity, Long> repository;

    public AppTeamJpaPersistenceService(EntityManager entityManager, Snowflake snowflake) {
        this.em = entityManager;
        this.snowflake = snowflake;
        this.repository = new SimpleJpaRepository<>(AppTeamJpaEntity.class, entityManager);
    }

    @Override
    public Optional<AppTeamJpaEntity> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Page<AppTeamJpaEntity> findAll(QueryAppGroup query, Pageable pageable) {
        return null;
    }

    @Override
    public AppTeamJpaEntity save(AppTeamJpaEntity entity) {
        return null;
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public AppTeamJpaEntity initNewInstance() {
        return new AppTeamJpaEntity(snowflake.nextId());
    }
}
