package io.rocketbase.commons.service.invite;

import io.rocketbase.commons.dto.appinvite.QueryAppInvite;
import io.rocketbase.commons.model.AppInviteEntity;
import io.rocketbase.commons.model.AppInviteJpaEntity;
import io.rocketbase.commons.model.AppInviteJpaEntity_;
import io.rocketbase.commons.service.JpaQueryHelper;
import io.rocketbase.commons.util.Nulls;
import io.rocketbase.commons.util.Snowflake;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Transactional
public class AppInviteJpaServiceImpl implements AppInvitePersistenceService<AppInviteJpaEntity>, JpaQueryHelper {

    private final EntityManager em;
    private final Snowflake snowflake;

    private final SimpleJpaRepository<AppInviteJpaEntity, Long> repository;

    public AppInviteJpaServiceImpl(EntityManager entityManager, Snowflake snowflake) {
        this.em = entityManager;
        this.snowflake = snowflake;
        this.repository = new SimpleJpaRepository<>(AppInviteJpaEntity.class, entityManager);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppInviteJpaEntity> findAll(QueryAppInvite query, Pageable pageable) {
        Specification<AppInviteJpaEntity> specification = (root, criteriaQuery, cb) -> {
            if (query == null) {
                return null;
            }
            Predicate result;
            if (!Nulls.notNull(query.getExpired(), false)) {
                result = cb.greaterThanOrEqualTo(root.get("expiration"), Instant.now());
            } else {
                result = cb.lessThan(root.get("expiration"), Instant.now());
            }

            List<Predicate> predicates = new ArrayList<>();
            addToListIfNotEmpty(predicates, query.getInvitor(), root.get(AppInviteJpaEntity_.INVITOR), cb);
            addToListIfNotEmpty(predicates, query.getEmail(), root.get(AppInviteJpaEntity_.EMAIL), cb);

            if (query.getKeyValues() != null && !query.getKeyValues().isEmpty()) {
                criteriaQuery.distinct(true);
                MapJoin<AppInviteJpaEntity, String, String> mapJoin = root.joinMap("keyValueMap");
                for (Map.Entry<String, String> keyEntry : query.getKeyValues().entrySet()) {
                    predicates.add(cb.and(cb.equal(mapJoin.key(), keyEntry.getKey()), cb.equal(mapJoin.value(), keyEntry.getValue())));
                }
            }

            if (!predicates.isEmpty()) {
                result = cb.and(result, cb.and(predicates.toArray(new Predicate[]{})));
            }
            return result;
        };
        return repository.findAll(specification, pageable);
    }

    @Override
    public Page<AppInviteEntity> findAllDto(QueryAppInvite query, Pageable pageable) {
        findAll(query, pageable);

        return null;
    }

    @Override
    public AppInviteJpaEntity save(AppInviteJpaEntity entity) {
        if (entity.getId() == null) {
            entity.setId(snowflake.nextId());
        }
        if (entity.getCreated() == null) {
            entity.setCreated(Instant.now());
        }
        return repository.save(entity);
    }

    @Override
    public AppInviteEntity saveDto(AppInviteEntity entity) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AppInviteJpaEntity> findById(Long id) {
        return Optional.ofNullable(em.find(AppInviteJpaEntity.class, id));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AppInviteEntity> findDtoById(Long id) {
        return Optional.empty();
    }


    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    void deleteAll() {
        repository.deleteAllInBatch();
    }

    @Override
    public long deleteExpired() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<AppInviteJpaEntity> delete = cb.createCriteriaDelete(AppInviteJpaEntity.class);
        Root<AppInviteJpaEntity> root = delete.from(AppInviteJpaEntity.class);
        delete.where(cb.lessThan(root.get(AppInviteJpaEntity_.EXPIRATION), Instant.now()));
        return em.createQuery(delete).executeUpdate();
    }
}
