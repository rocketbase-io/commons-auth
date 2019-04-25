package io.rocketbase.commons.service;

import io.rocketbase.commons.dto.appuser.QueryAppUser;
import io.rocketbase.commons.model.AppUserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class AppUserMongoServiceImpl implements AppUserPersistenceService<AppUserEntity> {

    private final MongoTemplate mongoTemplate;

    @Override
    public Optional<AppUserEntity> findByUsername(String username) {
        AppUserEntity entity = mongoTemplate.findOne(new Query(Criteria.where("username")
                .is(username)), AppUserEntity.class);
        if (entity != null) {
            return Optional.of(entity);
        }
        return Optional.empty();
    }

    @Override
    public Optional<AppUserEntity> findByEmail(String email) {
        AppUserEntity entity = mongoTemplate.findOne(new Query(Criteria.where("email")
                .is(email)), AppUserEntity.class);
        if (entity != null) {
            return Optional.of(entity);
        }
        return Optional.empty();
    }

    @Override
    public Page<AppUserEntity> findAll(Pageable pageable) {
        return findAll(null, pageable);
    }

    @Override
    public Page<AppUserEntity> findAll(QueryAppUser query, Pageable pageable) {
        List<AppUserEntity> entities = mongoTemplate.find(getQuery(query).with(pageable), AppUserEntity.class);
        long total = mongoTemplate.count(getQuery(query), AppUserEntity.class);

        return new PageImpl<>(entities, pageable, total);
    }

    private Query getQuery(QueryAppUser query) {
        Query result = new Query();
        if (query != null) {
            if (query.getUsername() != null) {
                result.addCriteria(Criteria.where("username").regex(query.getUsername(), "i"));
            }
            if (query.getFirstName() != null) {
                result.addCriteria(Criteria.where("firstName").regex(query.getFirstName(), "i"));
            }
            if (query.getLastName() != null) {
                result.addCriteria(Criteria.where("lastName").regex(query.getLastName(), "i"));
            }
            if (query.getEmail() != null) {
                result.addCriteria(Criteria.where("email").regex(query.getEmail(), "i"));
            }
            if (query.getEnabled() != null) {
                result.addCriteria(Criteria.where("enabled").is(query.getEnabled()));
            }
        }
        return result;
    }

    @Override
    public AppUserEntity save(AppUserEntity entity) {
        mongoTemplate.save(entity);
        return entity;
    }

    @Override
    public Optional<AppUserEntity> findById(String id) {
        AppUserEntity entity = mongoTemplate.findOne(new Query(Criteria.where("_id")
                .is(id)), AppUserEntity.class);
        if (entity != null) {
            return Optional.of(entity);
        }
        return Optional.empty();
    }

    @Override
    public long count() {
        return mongoTemplate.count(new Query(), AppUserEntity.class);
    }

    @Override
    public void delete(AppUserEntity entity) {
        mongoTemplate.remove(new Query(Criteria.where("_id")
                .is(entity.getId())), AppUserEntity.class);
    }

    @Override
    public void deleteAll() {
        mongoTemplate.findAllAndRemove(new Query(), AppUserEntity.class);
    }

    @Override
    public AppUserEntity initNewInstance() {
        return AppUserEntity.builder()
                .id(UUID.randomUUID().toString())
                .created(LocalDateTime.now())
                .roles(new ArrayList<>())
                .build();
    }
}
