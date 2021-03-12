package io.rocketbase.commons.service.capability;

import io.rocketbase.commons.dto.appcapability.QueryAppCapability;
import io.rocketbase.commons.model.AppCapabilityJpaEntity;
import io.rocketbase.commons.service.JpaQueryHelper;
import io.rocketbase.commons.util.Snowflake;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class AppCapabilityJpaPersistenceService implements AppCapabilityPersistenceService<AppCapabilityJpaEntity>, JpaQueryHelper {

    private final EntityManager em;
    private final Snowflake snowflake;

    private final SimpleJpaRepository<AppCapabilityJpaEntity, Long> repository;


    public AppCapabilityJpaPersistenceService(EntityManager entityManager, Snowflake snowflake) {
        this.em = entityManager;
        this.snowflake = snowflake;
        this.repository = new SimpleJpaRepository<>(AppCapabilityJpaEntity.class, entityManager);
    }

    @Override
    public Optional<AppCapabilityJpaEntity> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<AppCapabilityJpaEntity> findAllById(Iterable<Long> ids) {
        return repository.findAllById(ids);
    }

    @Override
    public Page<AppCapabilityJpaEntity> findAll(QueryAppCapability query, Pageable pageable) {
        return null;
    }

    @Override
    public AppCapabilityJpaEntity save(AppCapabilityJpaEntity entity) {
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
    public AppCapabilityJpaEntity initNewInstance() {
        return new AppCapabilityJpaEntity(snowflake.nextId());
    }
}
