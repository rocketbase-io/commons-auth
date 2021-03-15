package io.rocketbase.commons.service.group;

import com.google.common.collect.Sets;
import io.rocketbase.commons.dto.appgroup.QueryAppGroup;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.model.AppCapabilityJpaEntity;
import io.rocketbase.commons.model.AppGroupJpaEntity;
import io.rocketbase.commons.service.JpaQueryHelper;
import io.rocketbase.commons.util.Snowflake;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class AppGroupJpaPersistenceService implements AppGroupPersistenceService<AppGroupJpaEntity>, JpaQueryHelper {

    private final EntityManager em;
    private final Snowflake snowflake;

    private final SimpleJpaRepository<AppGroupJpaEntity, Long> repository;
    private final SimpleJpaRepository<AppCapabilityJpaEntity, Long> capabilityRepository;


    public AppGroupJpaPersistenceService(EntityManager entityManager, Snowflake snowflake) {
        this.em = entityManager;
        this.snowflake = snowflake;
        this.repository = new SimpleJpaRepository<>(AppGroupJpaEntity.class, entityManager);
        this.capabilityRepository = new SimpleJpaRepository<>(AppCapabilityJpaEntity.class, entityManager);
    }

    @Override
    public Optional<AppGroupJpaEntity> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<AppGroupJpaEntity> findAllById(Iterable<Long> ids) {
        return repository.findAllById(ids);
    }

    @Override
    public Page<AppGroupJpaEntity> findAll(QueryAppGroup query, Pageable pageable) {
        return null;
    }

    @Override
    public AppGroupJpaEntity save(AppGroupJpaEntity entity) {
        if (entity.getId() == null) {
            entity.setId(snowflake.nextId());
        }
        if (entity.getCreated() == null) {
            entity.setCreated(Instant.now());
        }
        if (entity.getParentHolder() != null) {
            entity.setParent(repository.findById(entity.getParentHolder()).orElseThrow(NotFoundException::new));
        }
        if (entity.getCapabilityHolder() != null) {
            entity.setCapabilities(Sets.newHashSet(capabilityRepository.findAllById(entity.getCapabilityHolder())));
        }

        return repository.save(entity);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public AppGroupJpaEntity initNewInstance() {
        return new AppGroupJpaEntity(snowflake.nextId());
    }
}
