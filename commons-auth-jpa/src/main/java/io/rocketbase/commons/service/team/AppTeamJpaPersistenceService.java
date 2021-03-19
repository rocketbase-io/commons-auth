package io.rocketbase.commons.service.team;

import io.rocketbase.commons.dto.appteam.QueryAppTeam;
import io.rocketbase.commons.model.AppTeamJpaEntity;
import io.rocketbase.commons.model.AppTeamJpaEntity_;
import io.rocketbase.commons.service.JpaQueryHelper;
import io.rocketbase.commons.util.Snowflake;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AppTeamJpaPersistenceService implements AppTeamPersistenceService<AppTeamJpaEntity>, JpaQueryHelper {

    private final EntityManager em;
    private final Snowflake snowflake;

    private final SimpleJpaRepository<AppTeamJpaEntity, Long> repository;

    public AppTeamJpaPersistenceService(EntityManager entityManager, Snowflake snowflake) {
        this.em = entityManager;
        this.snowflake = snowflake;
        this.repository = new SimpleJpaRepository<>(AppTeamJpaEntity.class, entityManager);
    }

    @Override
    public Optional<AppTeamJpaEntity> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<AppTeamJpaEntity> findAllById(Iterable<Long> ids) {
        return repository.findAllById(ids);
    }

    @Override
    public Page<AppTeamJpaEntity> findAll(QueryAppTeam query, Pageable pageable) {
        Specification<AppTeamJpaEntity> specification = (root, criteriaQuery, cb) -> {
            if (query == null) {
                return null;
            }
            List<Predicate> predicates = new ArrayList<>();
            if (query.getIds() != null && !query.getIds().isEmpty()) {
                predicates.add(root.get(AppTeamJpaEntity_.ID).in(query.getIds()));
            }
            addToListIfNotEmpty(predicates, query.getName(), root.get(AppTeamJpaEntity_.NAME), cb);
            addToListIfNotEmpty(predicates, query.getDescription(), root.get(AppTeamJpaEntity_.DESCRIPTION), cb);

            if (query.getPersonal() != null) {
                predicates.add(cb.equal(root.get(AppTeamJpaEntity_.PERSONAL), query.getPersonal()));
            }
            if (!predicates.isEmpty()) {
                return cb.and(predicates.toArray(new Predicate[]{}));
            }
            return null;
        };
        return repository.findAll(specification, pageable);
    }

    @Override
    public AppTeamJpaEntity save(AppTeamJpaEntity entity) {
        if (entity.getId() == null) {
            entity.setId(snowflake.nextId());
        }

        return repository.save(entity);
    }

    @Override
    public void delete(Long id) {
        em.createNativeQuery("update co_user set active_team_id = null where active_team_id = ?").setParameter(1, id).executeUpdate();

        repository.findById(id)
                .ifPresent(e -> {
                    e.setKeyValues(null);
                    e.setMembers(null);
                    repository.delete(e);
                });
    }

    @Override
    public AppTeamJpaEntity initNewInstance() {
        return new AppTeamJpaEntity(snowflake.nextId());
    }
}
