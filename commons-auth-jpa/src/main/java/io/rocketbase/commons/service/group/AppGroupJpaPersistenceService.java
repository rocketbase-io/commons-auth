package io.rocketbase.commons.service.group;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.rocketbase.commons.dto.appgroup.AppGroupRead;
import io.rocketbase.commons.dto.appgroup.QueryAppGroup;
import io.rocketbase.commons.exception.BadRequestException;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.model.AppCapabilityJpaEntity;
import io.rocketbase.commons.model.AppGroupEntity;
import io.rocketbase.commons.model.AppGroupJpaEntity;
import io.rocketbase.commons.model.AppGroupJpaEntity_;
import io.rocketbase.commons.service.CustomQueryMethodMetadata;
import io.rocketbase.commons.service.JpaQueryHelper;
import io.rocketbase.commons.util.Snowflake;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
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
        EntityGraph entityGraph = entityManager.getEntityGraph("co-group-entity-graph");
        repository.setRepositoryMethodMetadata(new CustomQueryMethodMetadata(entityGraph));
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
    public List<AppGroupJpaEntity> findAllByParentId(Iterable<Long> ids) {
        Specification<AppGroupJpaEntity> specification = (root, criteriaQuery, cb) -> {
            return cb.and(root.get(AppGroupJpaEntity_.PARENT).get(AppGroupJpaEntity_.ID).in(Lists.newArrayList(ids)));
        };
        return repository.findAll(specification);
    }

    @Override
    public Page<AppGroupJpaEntity> findAll(QueryAppGroup query, Pageable pageable) {
        Specification<AppGroupJpaEntity> specification = (root, criteriaQuery, cb) -> {
            if (query == null) {
                return null;
            }
            List<Predicate> predicates = new ArrayList<>();
            if (query.getIds() != null && !query.getIds().isEmpty()) {
                predicates.add(root.get(AppGroupJpaEntity_.ID).in(query.getIds()));
            }
            addToListIfNotEmpty(predicates, query.getNamePath(), root.get(AppGroupJpaEntity_.NAME_PATH), cb);
            addSystemRefIdToList(predicates, query.getSystemRefId(), root.get(AppGroupJpaEntity_.SYSTEM_REF_ID), cb);
            addToListIfNotEmpty(predicates, query.getName(), root.get(AppGroupJpaEntity_.NAME), cb);

            if (query.getParentIds() != null && !query.getParentIds().isEmpty()) {
                predicates.add(root.get(AppGroupJpaEntity_.PARENT).get(AppGroupJpaEntity_.ID).in(query.getParentIds()));
            }
            addToListIfNotEmpty(predicates, query.getDescription(), root.get(AppGroupJpaEntity_.DESCRIPTION), cb);

            if (query.getKeyValues() != null && !query.getKeyValues().isEmpty()) {
                criteriaQuery.distinct(true);
                MapJoin<AppGroupJpaEntity, String, String> mapJoin = root.joinMap(AppGroupJpaEntity_.KEY_VALUES);
                for (Map.Entry<String, String> keyEntry : query.getKeyValues().entrySet()) {
                    predicates.add(cb.and(cb.equal(mapJoin.key(), keyEntry.getKey()), cb.equal(mapJoin.value(), keyEntry.getValue())));
                }
            }
            if (query.getCapabilityIds() != null && !query.getCapabilityIds().isEmpty()) {
                criteriaQuery.distinct(true);
                predicates.add(root.join(AppGroupJpaEntity_.CAPABILITIES).get(AppGroupJpaEntity_.ID).in(query.getCapabilityIds()));
            }

            if (!predicates.isEmpty()) {
                return cb.and(predicates.toArray(new Predicate[]{}));
            }
            return null;
        };
        return repository.findAll(specification, pageable);
    }

    @Override
    public AppGroupJpaEntity save(AppGroupJpaEntity entity) {
        if (entity.getId() == null) {
            entity.setId(snowflake.nextId());
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
        if (AppGroupRead.ROOT.getId().equals(id)) {
            throw new BadRequestException("root is not deletable!");
        }
        Set<AppGroupJpaEntity> resolved = resolveTree(Arrays.asList(id));
        for (AppGroupJpaEntity e : resolved.stream().sorted(Comparator.comparing(AppGroupEntity::getDepth).reversed()).collect(Collectors.toList())) {
            em.createNativeQuery("delete from co_invite_group where group_id = ?").setParameter(1, e.getId()).executeUpdate();
            em.createNativeQuery("delete from co_user_group where group_id = ?").setParameter(1, e.getId()).executeUpdate();

            e.setCapabilities(null);
            e.setKeyValues(null);
            repository.delete(e);
        }
    }

    @Override
    public AppGroupJpaEntity initNewInstance() {
        return new AppGroupJpaEntity(snowflake.nextId());
    }
}
