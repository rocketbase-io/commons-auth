package io.rocketbase.commons.service.client;

import io.rocketbase.commons.dto.appclient.QueryAppClient;
import io.rocketbase.commons.model.AppClientJpaEntity;
import io.rocketbase.commons.service.JpaQueryHelper;
import io.rocketbase.commons.util.Snowflake;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class AppClientJpaPersistenceService implements AppClientPersistenceService<AppClientJpaEntity>, JpaQueryHelper {

    private final EntityManager em;
    private final Snowflake snowflake;

    private final SimpleJpaRepository<AppClientJpaEntity, Long> repository;


    public AppClientJpaPersistenceService(EntityManager entityManager, Snowflake snowflake) {
        this.em = entityManager;
        this.snowflake = snowflake;
        this.repository = new SimpleJpaRepository<>(AppClientJpaEntity.class, entityManager);
    }

    @Override
    public Optional<AppClientJpaEntity> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<AppClientJpaEntity> findAllById(Iterable<Long> ids) {
        return repository.findAllById(ids);
    }

    @Override
    public Page<AppClientJpaEntity> findAll(QueryAppClient query, Pageable pageable) {
        return null;
    }

    @Override
    public AppClientJpaEntity save(AppClientJpaEntity entity) {
        if (entity.getId() == null) {
            entity.setId(snowflake.nextId());
        }
        if (entity.getCreated() == null) {
            entity.setCreated(Instant.now());
        }

        return repository.save(entity);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public AppClientJpaEntity initNewInstance() {
        return new AppClientJpaEntity(snowflake.nextId());
    }
}
