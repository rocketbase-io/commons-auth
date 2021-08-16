package io.rocketbase.commons.service.client;

import com.google.common.collect.Sets;
import io.rocketbase.commons.dto.appclient.QueryAppClient;
import io.rocketbase.commons.model.*;
import io.rocketbase.commons.service.CustomQueryMethodMetadata;
import io.rocketbase.commons.service.JpaQueryHelper;
import io.rocketbase.commons.util.Snowflake;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional
public class AppClientJpaPersistenceService implements AppClientPersistenceService<AppClientJpaEntity>, JpaQueryHelper {

    private final EntityManager em;
    private final Snowflake snowflake;

    private final SimpleJpaRepository<AppClientJpaEntity, Long> repository;
    private final SimpleJpaRepository<AppCapabilityJpaEntity, Long> capabilityRepository;


    public AppClientJpaPersistenceService(EntityManager entityManager, Snowflake snowflake) {
        this.em = entityManager;
        this.snowflake = snowflake;
        this.repository = new SimpleJpaRepository<>(AppClientJpaEntity.class, entityManager);
        this.capabilityRepository = new SimpleJpaRepository<>(AppCapabilityJpaEntity.class, entityManager);
        EntityGraph entityGraph = entityManager.getEntityGraph("co-client-entity-graph");
        repository.setRepositoryMethodMetadata(new CustomQueryMethodMetadata(entityGraph));
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
        Specification<AppClientJpaEntity> specification = (root, criteriaQuery, cb) -> {
            if (query == null) {
                return null;
            }
            List<Predicate> predicates = new ArrayList<>();
            if (query.getIds() != null && !query.getIds().isEmpty()) {
                predicates.add(root.get(AppClientJpaEntity_.ID).in(query.getIds()));
            }
            if (query.getCapabilityIds() != null && !query.getCapabilityIds().isEmpty()) {
                criteriaQuery.distinct(true);
                predicates.add(root.join(AppUserJpaEntity_.CAPABILITIES).get(AppCapabilityJpaEntity_.ID).in(query.getCapabilityIds()));
            }

            addToListIfNotEmpty(predicates, query.getName(), root.get(AppClientJpaEntity_.NAME), cb);
            addToListIfNotEmpty(predicates, query.getDescription(), root.get(AppClientJpaEntity_.DESCRIPTION), cb);
            if (!StringUtils.isEmpty(query.getRedirectUrl())) {
                predicates.add(cb.like(cb.lower(root.get(AppClientJpaEntity_.REDIRECT_URLS).as(String.class)), buildLikeString(query.getRedirectUrl())));
            }
            addSystemRefIdToList(predicates, query.getSystemRefId(), root.get(AppClientJpaEntity_.SYSTEM_REF_ID), cb);

            if (!predicates.isEmpty()) {
                return cb.and(predicates.toArray(new Predicate[]{}));
            }
            return null;
        };
        return repository.findAll(specification, pageable);
    }

    @Override
    public AppClientJpaEntity save(AppClientJpaEntity entity) {
        if (entity.getId() == null) {
            entity.setId(snowflake.nextId());
        }
        if (entity.getCapabilityHolder() != null) {
            entity.setCapabilities(Sets.newHashSet(capabilityRepository.findAllById(entity.getCapabilityHolder())));
        }

        return repository.save(entity);
    }

    @Override
    public void delete(Long id) {
        repository.findById(id)
                .ifPresent(e -> {
                    e.setCapabilities(null);
                    repository.delete(e);
                });
    }

    @Override
    public AppClientJpaEntity initNewInstance() {
        return new AppClientJpaEntity(snowflake.nextId());
    }
}
