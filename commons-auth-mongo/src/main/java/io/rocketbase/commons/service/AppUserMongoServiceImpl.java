package io.rocketbase.commons.service;

import io.rocketbase.commons.dto.appuser.QueryAppUser;
import io.rocketbase.commons.model.AppUserMongoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class AppUserMongoServiceImpl implements AppUserPersistenceService<AppUserMongoEntity> {

    private final MongoTemplate mongoTemplate;

    @Override
    public Optional<AppUserMongoEntity> findByUsername(String username) {
        AppUserMongoEntity entity = mongoTemplate.findOne(new Query(Criteria.where("username")
                .is(username)), AppUserMongoEntity.class);
        if (entity != null) {
            return Optional.of(entity);
        }
        return Optional.empty();
    }

    @Override
    public Optional<AppUserMongoEntity> findByEmail(String email) {
        AppUserMongoEntity entity = mongoTemplate.findOne(new Query(Criteria.where("email")
                .is(email)), AppUserMongoEntity.class);
        if (entity != null) {
            return Optional.of(entity);
        }
        return Optional.empty();
    }

    @Override
    public Page<AppUserMongoEntity> findAll(Pageable pageable) {
        return findAll(null, pageable);
    }

    @Override
    public Page<AppUserMongoEntity> findAll(QueryAppUser query, Pageable pageable) {
        List<AppUserMongoEntity> entities = mongoTemplate.find(getQuery(query).with(pageable), AppUserMongoEntity.class);
        long total = mongoTemplate.count(getQuery(query), AppUserMongoEntity.class);

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
    public AppUserMongoEntity save(AppUserMongoEntity entity) {
        mongoTemplate.save(entity);
        return entity;
    }

    @Override
    public Optional<AppUserMongoEntity> findById(String id) {
        AppUserMongoEntity entity = mongoTemplate.findOne(new Query(Criteria.where("_id")
                .is(id)), AppUserMongoEntity.class);
        if (entity != null) {
            return Optional.of(entity);
        }
        return Optional.empty();
    }

    @Override
    public long count() {
        return mongoTemplate.count(new Query(), AppUserMongoEntity.class);
    }

    @Override
    public void delete(AppUserMongoEntity entity) {
        mongoTemplate.remove(new Query(Criteria.where("_id")
                .is(entity.getId())), AppUserMongoEntity.class);
    }

    @Override
    public void deleteAll() {
        mongoTemplate.findAllAndRemove(new Query(), AppUserMongoEntity.class);
    }

    @Override
    public AppUserMongoEntity initNewInstance() {
        return AppUserMongoEntity.builder()
                .id(UUID.randomUUID().toString())
                .created(Instant.now())
                .roles(new ArrayList<>())
                .build();
    }
}
