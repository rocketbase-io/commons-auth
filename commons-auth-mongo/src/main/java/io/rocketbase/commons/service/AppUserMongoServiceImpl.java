package io.rocketbase.commons.service;

import io.rocketbase.commons.dto.appuser.QueryAppUser;
import io.rocketbase.commons.model.AppUserMongoEntity;
import io.rocketbase.commons.util.Nulls;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;

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
    public Page<AppUserMongoEntity> findAll(QueryAppUser query, Pageable pageable) {
        List<AppUserMongoEntity> entities = mongoTemplate.find(getQuery(query).with(pageable), AppUserMongoEntity.class);
        long total = mongoTemplate.count(getQuery(query), AppUserMongoEntity.class);

        return new PageImpl<>(entities, pageable, total);
    }

    private Query getQuery(QueryAppUser query) {
        Query result = new Query();
        if (query != null) {
            if (!StringUtils.isEmpty(query.getUsername())) {
                result.addCriteria(buildRegexCriteria("username", query.getUsername()));
            }
            if (!StringUtils.isEmpty(query.getFirstName())) {
                result.addCriteria(buildRegexCriteria("firstName", query.getFirstName()));
            }
            if (!StringUtils.isEmpty(query.getLastName())) {
                result.addCriteria(buildRegexCriteria("lastName", query.getLastName()));
            }
            if (!StringUtils.isEmpty(query.getEmail())) {
                result.addCriteria(buildRegexCriteria("email", query.getEmail()));
            }
            if (!StringUtils.isEmpty(query.getFreetext())) {
                result.addCriteria(new Criteria().orOperator(buildRegexCriteria("username", query.getFreetext()),
                        buildRegexCriteria("firstName", query.getFreetext()),
                        buildRegexCriteria("lastName", query.getFreetext()),
                        buildRegexCriteria("email", query.getFreetext())));
            }
            result.addCriteria(Criteria.where("enabled").is(Nulls.notNull(query.getEnabled(), true)));
        }
        return result;
    }

    private Criteria buildRegexCriteria(String where, String text) {
        String pattern = text.trim() + "";
        if (!pattern.contains(".*")) {
            pattern = ".*" + pattern + ".*";
        }
        return Criteria.where(where).regex(pattern, "i");
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
