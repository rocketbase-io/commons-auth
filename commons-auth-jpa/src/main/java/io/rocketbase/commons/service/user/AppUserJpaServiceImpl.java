package io.rocketbase.commons.service.user;

import com.google.common.collect.Sets;
import io.rocketbase.commons.dto.appuser.QueryAppUser;
import io.rocketbase.commons.model.*;
import io.rocketbase.commons.model.embedded.UserProfileJpaEmbedded_;
import io.rocketbase.commons.service.JpaQueryHelper;
import io.rocketbase.commons.util.Nulls;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class AppUserJpaServiceImpl implements AppUserPersistenceService<AppUserJpaEntity>, JpaQueryHelper {

    private final EntityManager entityManager;
    private final SimpleJpaRepository<AppUserJpaEntity, String> repository;
    private final SimpleJpaRepository<AppGroupJpaEntity, Long> groupRepository;
    private final SimpleJpaRepository<AppCapabilityJpaEntity, Long> capabilityRepository;
    private final SimpleJpaRepository<AppTeamJpaEntity, Long> teamRepository;

    public AppUserJpaServiceImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
        repository = new SimpleJpaRepository<>(AppUserJpaEntity.class, entityManager);
        groupRepository = new SimpleJpaRepository<>(AppGroupJpaEntity.class, entityManager);
        capabilityRepository = new SimpleJpaRepository<>(AppCapabilityJpaEntity.class, entityManager);
        teamRepository = new SimpleJpaRepository<>(AppTeamJpaEntity.class, entityManager);
    }

    @Override
    public Optional<AppUserJpaEntity> findByUsername(String username) {
        if (username == null) {
            return Optional.empty();
        }
        Specification<AppUserJpaEntity> specification = (root, criteriaQuery, cb) -> cb.in(root.get(AppUserJpaEntity_.USERNAME).in(username.toLowerCase()));
        return repository.findOne(specification);
    }

    @Override
    public Optional<AppUserJpaEntity> findByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }
        Specification<AppUserJpaEntity> specification = (root, criteriaQuery, cb) -> cb.in(root.get(AppUserJpaEntity_.EMAIL).in(email.toLowerCase()));
        return repository.findOne(specification);
    }

    @Override
    public Optional<AppUserJpaEntity> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Page<AppUserJpaEntity> findAll(QueryAppUser query, Pageable pageable) {
        if (query == null || query.isEmpty()) {
            return repository.findAll(pageable);
        }

        Specification<AppUserJpaEntity> specification = (root, criteriaQuery, cb) -> {
            Predicate result = cb.equal(root.get(AppUserJpaEntity_.ENABLED), Nulls.notNull(query.getEnabled(), true));
            // explicit search for given property
            List<Predicate> explicitSearch = new ArrayList<>();
            addToListIfNotEmpty(explicitSearch, query.getUsername(), root.get(AppUserJpaEntity_.USERNAME), cb);
            addToListIfNotEmpty(explicitSearch, query.getFirstName(), root.get(AppUserJpaEntity_.PROFILE).get(UserProfileJpaEmbedded_.FIRST_NAME), cb);
            addToListIfNotEmpty(explicitSearch, query.getLastName(), root.get(AppUserJpaEntity_.PROFILE).get(UserProfileJpaEmbedded_.LAST_NAME), cb);
            addToListIfNotEmpty(explicitSearch, query.getEmail(), root.get(AppUserJpaEntity_.EMAIL), cb);
            if (!explicitSearch.isEmpty()) {
                result = cb.and(result, cb.and(explicitSearch.toArray(new Predicate[]{})));
            }

            // freetext-search combine all properties with or (rest normal by and)
            if (StringUtils.isEmpty(query.getFreetext())) {
                List<Predicate> freeSearch = new ArrayList<>();
                addToListIfNotEmpty(freeSearch, query.getFreetext(), root.get(AppUserJpaEntity_.USERNAME), cb);
                addToListIfNotEmpty(freeSearch, query.getFreetext(), root.get(AppUserJpaEntity_.PROFILE).get(UserProfileJpaEmbedded_.FIRST_NAME), cb);
                addToListIfNotEmpty(freeSearch, query.getFreetext(), root.get(AppUserJpaEntity_.PROFILE).get(UserProfileJpaEmbedded_.LAST_NAME), cb);
                addToListIfNotEmpty(freeSearch, query.getFreetext(), root.get(AppUserJpaEntity_.EMAIL), cb);
                result = cb.and(result, cb.or(explicitSearch.toArray(new Predicate[]{})));
            }

            List<Predicate> furtherFilters = new ArrayList<>();
            if (query.getCapabilityIds() != null && !query.getCapabilityIds().isEmpty()) {
                criteriaQuery.distinct(true);
                furtherFilters.add(root.join(AppUserJpaEntity_.CAPABILITIES).in(query.getCapabilityIds()));
            }
            if (query.getGroupIds() != null && !query.getGroupIds().isEmpty()) {
                criteriaQuery.distinct(true);
                furtherFilters.add(root.join(AppUserJpaEntity_.GROUPS).in(query.getGroupIds()));
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

        return repository.findAll(specification, pageable);
    }

    @Override
    public AppUserJpaEntity save(AppUserJpaEntity entity) {
        if (entity.getGroupHolder() != null) {
            List<AppGroupJpaEntity> lookupGroups = groupRepository.findAllById(entity.getGroupHolder());
            entity.setGroups(Sets.newHashSet(lookupGroups));
        }
        if (entity.getCapabilityHolder() != null) {
            List<AppCapabilityJpaEntity> lookupCapabilities = capabilityRepository.findAllById(entity.getCapabilityHolder());
            entity.setCapabilities(Sets.newHashSet(lookupCapabilities));
        }
        if (entity.getActiveTeamHolder() != null) {
            Optional<AppTeamJpaEntity> lookupTeam = teamRepository.findById(entity.getActiveTeamId());
            entity.setActiveTeam(lookupTeam.orElse(null));
            if (!lookupTeam.isPresent()) {
                log.warn("set activeTeamId: {} not found. set for appUser: {} activeTeam to null", entity.getActiveTeamHolder(), entity.getId());
            }
        }
        return repository.save(entity);
    }

    @Override
    public void delete(String id) {
        repository.deleteById(id);
    }

    void deleteAll() {
        repository.deleteAllInBatch();
    }

    protected Set<Long> lookCapabilities(Set<String> capabilities) {
        Specification<AppCapabilityJpaEntity> specification = (root, criteriaQuery, cb) -> cb.in(root.get(AppCapabilityJpaEntity_.KEY_PATH).in(capabilities));
        return capabilityRepository.findAll(specification).stream().map(AppCapabilityJpaEntity::getId).collect(Collectors.toSet());
    }

}
