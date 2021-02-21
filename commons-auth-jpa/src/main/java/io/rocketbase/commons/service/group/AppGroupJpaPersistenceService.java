package io.rocketbase.commons.service.group;

import io.rocketbase.commons.dto.appgroup.QueryAppGroup;
import io.rocketbase.commons.model.AppGroupJpaEntity;
import io.rocketbase.commons.service.JpaQueryHelper;
import io.rocketbase.commons.util.Snowflake;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class AppGroupJpaPersistenceService implements AppGroupPersistenceService<AppGroupJpaEntity>, JpaQueryHelper {

    private final EntityManager em;
    private final Snowflake snowflake;

    private final SimpleJpaRepository<AppGroupJpaEntity, Long> repository;


    public AppGroupJpaPersistenceService(EntityManager entityManager, Snowflake snowflake) {
        this.em = entityManager;
        this.snowflake = snowflake;
        this.repository = new SimpleJpaRepository<>(AppGroupJpaEntity.class, entityManager);
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
        return null;
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
