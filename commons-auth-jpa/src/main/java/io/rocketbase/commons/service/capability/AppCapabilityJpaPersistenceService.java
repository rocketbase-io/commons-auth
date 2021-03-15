package io.rocketbase.commons.service.capability;

import io.rocketbase.commons.dto.appcapability.QueryAppCapability;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.model.AppCapabilityJpaEntity;
import io.rocketbase.commons.model.AppCapabilityJpaEntity_;
import io.rocketbase.commons.service.JpaQueryHelper;
import io.rocketbase.commons.util.Snowflake;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    public List<AppCapabilityJpaEntity> findAllByParentId(Iterable<Long> ids) {
        Specification<AppCapabilityJpaEntity> specification = (root, criteriaQuery, cb) -> {
            return cb.and(root.get(AppCapabilityJpaEntity_.PARENT).get(AppCapabilityJpaEntity_.ID).in(ids));
        };
        return repository.findAll(specification);
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
        if (entity.getParentHolder() != null) {
            entity.setParent(repository.findById(entity.getParentHolder()).orElseThrow(NotFoundException::new));
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
