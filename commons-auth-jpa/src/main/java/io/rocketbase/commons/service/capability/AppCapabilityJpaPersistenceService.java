package io.rocketbase.commons.service.capability;

import com.google.common.collect.Lists;
import io.rocketbase.commons.dto.appcapability.QueryAppCapability;
import io.rocketbase.commons.exception.BadRequestException;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.model.AppCapabilityEntity;
import io.rocketbase.commons.model.AppCapabilityJpaEntity;
import io.rocketbase.commons.model.AppCapabilityJpaEntity_;
import io.rocketbase.commons.service.CustomQueryMethodMetadata;
import io.rocketbase.commons.service.JpaQueryHelper;
import io.rocketbase.commons.util.Snowflake;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;

import static io.rocketbase.commons.dto.appcapability.AppCapabilityRead.ROOT;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

@Slf4j
@Transactional
public class AppCapabilityJpaPersistenceService implements AppCapabilityPersistenceService<AppCapabilityJpaEntity>, JpaQueryHelper, InitializingBean {

    private final PlatformTransactionManager transactionManager;
    private final EntityManager em;
    private final Snowflake snowflake;
    private final boolean initializeRoot;

    private final SimpleJpaRepository<AppCapabilityJpaEntity, Long> repository;


    public AppCapabilityJpaPersistenceService(PlatformTransactionManager transactionManager, EntityManager entityManager, Snowflake snowflake, boolean initializeRoot) {
        this.transactionManager = transactionManager;
        this.em = entityManager;
        this.snowflake = snowflake;
        this.initializeRoot = initializeRoot;

        this.repository = new SimpleJpaRepository<>(AppCapabilityJpaEntity.class, entityManager);
        EntityGraph entityGraph = entityManager.getEntityGraph("co-capability-entity-graph");
        repository.setRepositoryMethodMetadata(new CustomQueryMethodMetadata(entityGraph));
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
            return cb.and(root.get(AppCapabilityJpaEntity_.PARENT).get(AppCapabilityJpaEntity_.ID).in(Lists.newArrayList(ids)),
                    cb.notEqual(root.get(AppCapabilityJpaEntity_.ID), ROOT.getId()));
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
        if (ROOT.getId().equals(id)) {
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

    @Override
    public void ensureInitializedRoot() {
        if (findAllById(Arrays.asList(ROOT.getId())).isEmpty()) {
            DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
            definition.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
            definition.setTimeout(3);

            TransactionStatus status = transactionManager.getTransaction(definition);

            try {
                em.createNativeQuery("insert into co_capacity (id, system_ref_id, key_, description, parent_id, key_path, with_children, created, modified_by, modified) " +
                                "values (:id, :systemRefId, :key, :description, :parentId, :keyPath, :withChildren, :created, :modifiedBy, :modified)")
                        .setParameter("id", ROOT.getId())
                        .setParameter("systemRefId", ROOT.getSystemRefId())
                        .setParameter("key", ROOT.getKey())
                        .setParameter("description", ROOT.getDescription())
                        .setParameter("parentId", ROOT.getParentId())
                        .setParameter("keyPath", ROOT.getKeyPath())
                        .setParameter("withChildren", false)
                        .setParameter("created", ROOT.getCreated())
                        .setParameter("modifiedBy", ROOT.getModifiedBy())
                        .setParameter("modified", ROOT.getModified())
                        .executeUpdate();
                transactionManager.commit(status);
                log.debug("root capability persisted");
            } catch (Exception e) {
                transactionManager.rollback(status);
                log.error("couldn't persist root capability: {}", e.getMessage());
            }
        }
    }


    @Transactional(propagation = NOT_SUPPORTED)
    @Override
    public void afterPropertiesSet() throws Exception {
        if (initializeRoot) {
            ensureInitializedRoot();
        }
    }
}
