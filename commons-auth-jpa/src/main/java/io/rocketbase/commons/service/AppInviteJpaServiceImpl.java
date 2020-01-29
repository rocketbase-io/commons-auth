package io.rocketbase.commons.service;

import io.rocketbase.commons.dto.appinvite.QueryAppInvite;
import io.rocketbase.commons.model.AppInviteJpaEntity;
import io.rocketbase.commons.model.AppUserJpaEntity;
import io.rocketbase.commons.repository.AppInviteJpaRepository;
import io.rocketbase.commons.util.Nulls;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.time.Instant;
import java.util.*;

@RequiredArgsConstructor
public class AppInviteJpaServiceImpl implements AppInvitePersistenceService<AppInviteJpaEntity>, PredicateHelper {

    private final AppInviteJpaRepository repository;

    @Override
    public Page<AppInviteJpaEntity> findAll(QueryAppInvite query, Pageable pageable) {
        if (query == null) {
            return repository.findAll(pageable);
        }

        Specification<AppInviteJpaEntity> specification = (Specification<AppInviteJpaEntity>) (root, criteriaQuery, cb) -> {
            Predicate result;
            if (!Nulls.notNull(query.getExpired(), false)) {
                result = cb.greaterThanOrEqualTo(root.get("expiration"), Instant.now());
            } else {
                result = cb.lessThan(root.get("expiration"), Instant.now());
            }

            List<Predicate> predicates = new ArrayList<>();
            addToListIfNotEmpty(predicates, query.getInvitor(), "invitor", root, cb);
            addToListIfNotEmpty(predicates, query.getEmail(), "email", root, cb);

            if (query.getKeyValues() != null && !query.getKeyValues().isEmpty()) {
                criteriaQuery.distinct(true);
                MapJoin<AppUserJpaEntity, String, String> mapJoin = root.joinMap("keyValueMap");
                for (Map.Entry<String, String> keyEntry : query.getKeyValues().entrySet()) {
                    predicates.add(cb.and(cb.equal(mapJoin.key(), keyEntry.getKey().toLowerCase()), cb.equal(cb.lower(mapJoin.value()), keyEntry.getValue().toLowerCase())));
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
    @Transactional
    public AppInviteJpaEntity save(AppInviteJpaEntity entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<AppInviteJpaEntity> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    @Transactional
    public void delete(AppInviteJpaEntity entity) {
        repository.delete(entity);
    }

    @Override
    @Transactional
    public void deleteAll() {
        repository.deleteAll();
    }

    @Override
    public AppInviteJpaEntity initNewInstance() {
        return AppInviteJpaEntity.builder()
                .id(UUID.randomUUID().toString())
                .created(Instant.now())
                .roles(new ArrayList<>())
                .build();
    }

    @Override
    @Transactional
    public long deleteExpired() {
        Page<AppInviteJpaEntity> allExpired = findAll(QueryAppInvite.builder().expired(true).build(), Pageable.unpaged());
        repository.deleteAll(allExpired.getContent());
        return allExpired.getNumberOfElements();
    }
}
