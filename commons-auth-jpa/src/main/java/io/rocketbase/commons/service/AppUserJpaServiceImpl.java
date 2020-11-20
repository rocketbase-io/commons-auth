package io.rocketbase.commons.service;

import io.rocketbase.commons.dto.appuser.QueryAppUser;
import io.rocketbase.commons.model.AppUserJpaEntity;
import io.rocketbase.commons.repository.AppUserJpaRepository;
import io.rocketbase.commons.util.Nulls;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.time.Instant;
import java.util.*;

@RequiredArgsConstructor
public class AppUserJpaServiceImpl implements AppUserPersistenceService<AppUserJpaEntity>, PredicateHelper {

    private final AppUserJpaRepository repository;

    @Override
    public Optional<AppUserJpaEntity> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    @Override
    public Optional<AppUserJpaEntity> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    @Transactional
    public Page<AppUserJpaEntity> findAll(QueryAppUser query, Pageable pageable) {
        if (query == null || query.isEmpty()) {
            return repository.findAll(pageable);
        }

        Specification<AppUserJpaEntity> specification = (Specification<AppUserJpaEntity>) (root, criteriaQuery, cb) -> {
            Predicate result = cb.equal(root.get("enabled"), Nulls.notNull(query.getEnabled(), true));
            List<Predicate> textSearch = new ArrayList<>();
            addToListIfNotEmpty(textSearch, Nulls.notEmpty(query.getUsername(), query.getFreetext()), "username", root, cb);
            addToListIfNotEmpty(textSearch, Nulls.notEmpty(query.getFirstName(), query.getFreetext()), "firstName", root, cb);
            addToListIfNotEmpty(textSearch, Nulls.notEmpty(query.getLastName(), query.getFreetext()), "lastName", root, cb);
            addToListIfNotEmpty(textSearch, Nulls.notEmpty(query.getEmail(), query.getFreetext()), "email", root, cb);
            if (!textSearch.isEmpty()) {
                if (StringUtils.isEmpty(query.getFreetext())) {
                    result = cb.and(result, cb.and(textSearch.toArray(new Predicate[]{})));
                } else {
                    result = cb.and(result, cb.or(textSearch.toArray(new Predicate[]{})));
                }
            }

            List<Predicate> furtherFilters = new ArrayList<>();
            if (!StringUtils.isEmpty(query.getHasRole())) {
                criteriaQuery.distinct(true);
                Predicate roles = cb.upper(root.join("roles")).in(query.getHasRole().toUpperCase());
                furtherFilters.add(roles);
            }
            if (query.getKeyValues() != null && !query.getKeyValues().isEmpty()) {
                criteriaQuery.distinct(true);
                MapJoin<AppUserJpaEntity, String, String> mapJoin = root.joinMap("keyValueMap");
                for (Map.Entry<String, String> keyEntry : query.getKeyValues().entrySet()) {
                    furtherFilters.add(cb.and(cb.equal(mapJoin.key(), keyEntry.getKey()), cb.equal(mapJoin.value(), keyEntry.getValue())));
                }
            }
            if (!furtherFilters.isEmpty()) {
                result = cb.and(result, cb.and(furtherFilters.toArray(new Predicate[]{})));
            }
            return result;
        };
        Page<AppUserJpaEntity> result = repository.findAll(specification, pageable);
        // in order to initialize lazy map
        result.stream()
                .forEach(v -> initLazyObjects(v));
        return result;
    }

    @Override
    @Transactional
    public AppUserJpaEntity save(AppUserJpaEntity entity) {
        return initLazyObjects(repository.save(entity));
    }

    @Override
    public Optional<AppUserJpaEntity> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    @Transactional
    public void delete(AppUserJpaEntity entity) {
        repository.delete(entity);
    }

    @Override
    @Transactional
    public void deleteAll() {
        repository.deleteAll();
    }

    @Override
    public AppUserJpaEntity initNewInstance() {
        return AppUserJpaEntity.builder()
                .id(UUID.randomUUID().toString())
                .created(Instant.now())
                .roles(new ArrayList<>())
                .build();
    }

    protected AppUserJpaEntity initLazyObjects(AppUserJpaEntity entity) {
        if (entity != null) {
            if (entity.getKeyValueMap() != null) {
                // in order to initialize lazy map
                entity.getKeyValueMap().size();
            }
            if (entity.getRoles() != null) {
                // in order to initialize lazy map
                entity.getRoles().size();
            }
        }
        return entity;
    }
}
