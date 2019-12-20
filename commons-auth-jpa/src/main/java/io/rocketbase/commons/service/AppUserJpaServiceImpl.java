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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class AppUserJpaServiceImpl implements AppUserPersistenceService<AppUserJpaEntity> {

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
    public Page<AppUserJpaEntity> findAll(QueryAppUser query, Pageable pageable) {
        if (query == null) {
            return repository.findAll(pageable);
        }

        Specification<AppUserJpaEntity> specification = new Specification<AppUserJpaEntity>() {
            @Override
            public Predicate toPredicate(Root<AppUserJpaEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                Predicate result = cb.equal(root.get("enabled"), Nulls.notNull(query.getEnabled(), true));
                List<Predicate> predicates = new ArrayList<>();
                addToListIfNotEmpty(predicates, Nulls.notEmpty(query.getUsername(), query.getFreetext()), "username", root, cb);
                addToListIfNotEmpty(predicates, Nulls.notEmpty(query.getFirstName(), query.getFreetext()), "firstName", root, cb);
                addToListIfNotEmpty(predicates, Nulls.notEmpty(query.getLastName(), query.getFreetext()), "lastName", root, cb);
                addToListIfNotEmpty(predicates, Nulls.notEmpty(query.getEmail(), query.getFreetext()), "email", root, cb);
                if (!predicates.isEmpty()) {
                    if (StringUtils.isEmpty(query.getFreetext())) {
                        result = cb.and(result, cb.and(predicates.toArray(new Predicate[]{})));
                    } else {
                        result = cb.and(result, cb.or(predicates.toArray(new Predicate[]{})));
                    }
                }

                if (!StringUtils.isEmpty(query.getHasRole())) {
                    Predicate roles = cb.upper(root.join("roles")).in(query.getHasRole().toUpperCase());
                    result = cb.and(result, roles);
                }
                return result;
            }
        };
        return repository.findAll(specification, pageable);
    }

    protected void addToListIfNotEmpty(List<Predicate> list, String value, String path, Root<AppUserJpaEntity> root, CriteriaBuilder cb) {
        if (!StringUtils.isEmpty(value)) {
            list.add(cb.like(cb.lower(root.get(path)), buildLikeString(value)));
        }
    }

    protected String buildLikeString(String value) {
        if (value.contains("*")) {
            return value.trim().toLowerCase().replace("*", "%");
        }
        return "%" + value.trim().toLowerCase() + "%";
    }

    @Override
    public AppUserJpaEntity save(AppUserJpaEntity entity) {
        return repository.save(entity);
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
    public void delete(AppUserJpaEntity entity) {
        repository.delete(entity);
    }

    @Override
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
}
