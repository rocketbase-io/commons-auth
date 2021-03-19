package io.rocketbase.commons.service.capability;

import com.google.common.collect.Lists;
import io.rocketbase.commons.dto.appcapability.AppCapabilityRead;
import io.rocketbase.commons.dto.appcapability.QueryAppCapability;
import io.rocketbase.commons.exception.BadRequestException;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.model.AppCapabilityEntity;
import io.rocketbase.commons.model.AppCapabilityJpaEntity;
import io.rocketbase.commons.model.AppCapabilityJpaEntity_;
import io.rocketbase.commons.service.JpaQueryHelper;
import io.rocketbase.commons.util.Snowflake;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;

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
            return cb.and(root.get(AppCapabilityJpaEntity_.PARENT).get(AppCapabilityJpaEntity_.ID).in(Lists.newArrayList(ids)));
        };
        return repository.findAll(specification);
    }

    @Override
    public Page<AppCapabilityJpaEntity> findAll(QueryAppCapability query, Pageable pageable) {
        Specification<AppCapabilityJpaEntity> specification = (root, criteriaQuery, cb) -> {
            if (query == null) {
                return null;
            }
            List<Predicate> predicates = new ArrayList<>();
            if (query.getIds() != null && !query.getIds().isEmpty()) {
                predicates.add(root.get(AppCapabilityJpaEntity_.ID).in(query.getIds()));
            }
            addToListIfNotEmpty(predicates, query.getKeyPath(), root.get(AppCapabilityJpaEntity_.KEY_PATH), cb);
            if (query.getParentIds() != null && !query.getParentIds().isEmpty()) {
                predicates.add(root.get(AppCapabilityJpaEntity_.PARENT).get(AppCapabilityJpaEntity_.ID).in(query.getParentIds()));
            }
            addToListIfNotEmpty(predicates, query.getKey(), root.get(AppCapabilityJpaEntity_.KEY), cb);
            addToListIfNotEmpty(predicates, query.getDescription(), root.get(AppCapabilityJpaEntity_.DESCRIPTION), cb);

            if (!predicates.isEmpty()) {
                return cb.and(predicates.toArray(new Predicate[]{}));
            }
            return null;
        };
        return repository.findAll(specification, pageable);
    }

    @Override
    public AppCapabilityJpaEntity save(AppCapabilityJpaEntity entity) {
        if (entity.getId() == null) {
            entity.setId(snowflake.nextId());
        }
        if (entity.getParentHolder() != null) {
            entity.setParent(repository.findById(entity.getParentHolder()).orElseThrow(NotFoundException::new));
        }

        return repository.save(entity);
    }

    @Override
    public void delete(Long id) {
        if (AppCapabilityRead.ROOT.getId().equals(id)) {
            throw new BadRequestException("root is not deletable!");
        }
        Set<AppCapabilityJpaEntity> resolved = resolveTree(Arrays.asList(id));
        for (AppCapabilityJpaEntity e : resolved.stream().sorted(Comparator.comparing(AppCapabilityEntity::getDepth).reversed()).collect(Collectors.toList())) {

            // group's capabilities
            em.createNativeQuery("delete from co_group_capability where capability_id = ?").setParameter(1, e.getId()).executeUpdate();
            // client's capabilities
            em.createNativeQuery("delete from co_client_capability where capability_id = ?").setParameter(1, e.getId()).executeUpdate();
            // invite's capabilities
            em.createNativeQuery("delete from co_invite_capability where capability_id = ?").setParameter(1, e.getId()).executeUpdate();
            // user's capabilities
            em.createNativeQuery("delete from co_user_capability where capability_id = ?").setParameter(1, e.getId()).executeUpdate();

            repository.deleteById(e.getId());
        }
    }

    @Override
    public AppCapabilityJpaEntity initNewInstance() {
        return new AppCapabilityJpaEntity(snowflake.nextId());
    }
}
