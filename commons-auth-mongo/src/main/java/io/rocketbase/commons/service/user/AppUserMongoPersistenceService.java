package io.rocketbase.commons.service.user;

import com.google.common.collect.Lists;
import io.rocketbase.commons.dto.appuser.QueryAppUser;
import io.rocketbase.commons.model.AppUserMongoEntity;
import io.rocketbase.commons.service.MongoQueryHelper;
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
import java.util.*;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class AppUserMongoPersistenceService implements AppUserPersistenceService<AppUserMongoEntity>, MongoQueryHelper {

    private final MongoTemplate mongoTemplate;

    private final String collectionName;

    @Override
    public Optional<AppUserMongoEntity> findByUsername(String username) {
        AppUserMongoEntity entity = mongoTemplate.findOne(new Query(Criteria.where("username")
                .is(username)), AppUserMongoEntity.class, collectionName);
        if (entity != null) {
            return Optional.of(entity);
        }
        return Optional.empty();
    }

    @Override
    public Optional<AppUserMongoEntity> findByEmail(String email) {
        AppUserMongoEntity entity = mongoTemplate.findOne(new Query(Criteria.where("email")
                .is(email)), AppUserMongoEntity.class, collectionName);
        if (entity != null) {
            return Optional.of(entity);
        }
        return Optional.empty();
    }

    @Override
    public Page<AppUserMongoEntity> findAll(QueryAppUser query, Pageable pageable) {
        List<AppUserMongoEntity> entities = mongoTemplate.find(getQuery(query).with(pageable), AppUserMongoEntity.class, collectionName);
        long total = mongoTemplate.count(getQuery(query), AppUserMongoEntity.class, collectionName);

        return new PageImpl<>(entities, pageable, total);
    }

    Query getQuery(QueryAppUser query) {
        Query result = new Query();
        if (query != null) {
            if (!StringUtils.isEmpty(query.getUsername())) {
                result.addCriteria(buildRegexCriteria("username", query.getUsername()));
            }
            if (!StringUtils.isEmpty(query.getFirstName())) {
                result.addCriteria(buildRegexCriteria("profile.firstName", query.getFirstName()));
            }
            if (!StringUtils.isEmpty(query.getLastName())) {
                result.addCriteria(buildRegexCriteria("profile.lastName", query.getLastName()));
            }
            if (!StringUtils.isEmpty(query.getEmail())) {
                result.addCriteria(buildRegexCriteria("email", query.getEmail()));
            }
            if (!StringUtils.isEmpty(query.getFreetext())) {
                result.addCriteria(new Criteria().orOperator(buildRegexCriteria("username", query.getFreetext()),
                        buildRegexCriteria("profile.firstName", query.getFreetext()),
                        buildRegexCriteria("profile.lastName", query.getFreetext()),
                        buildRegexCriteria("email", query.getFreetext())));
            }
            if (query.getCapabilityIds() != null) {
                result.addCriteria(Criteria.where("capabilityIds").in(query.getCapabilityIds()));
            }
            if (query.getGroupIds() != null) {
                result.addCriteria(Criteria.where("groupIds").in(query.getGroupIds()));
            }
            if (query.getKeyValues() != null && !query.getKeyValues().isEmpty()) {
                for (Map.Entry<String, String> kv : query.getKeyValues().entrySet()) {
                    Pattern valuePattern = Pattern.compile(kv.getValue(), Pattern.CASE_INSENSITIVE);
                    result.addCriteria(Criteria.where("keyValues." + kv.getKey()).is(valuePattern));
                }
            }
            result.addCriteria(Criteria.where("enabled").is(Nulls.notNull(query.getEnabled(), true)));
        }
        return result;
    }

    @Override
    public AppUserMongoEntity save(AppUserMongoEntity entity) {
        mongoTemplate.save(entity, collectionName);
        return entity;
    }

    @Override
    public Optional<AppUserMongoEntity> findById(String id) {
        AppUserMongoEntity entity = mongoTemplate.findOne(new Query(Criteria.where("_id")
                .is(id)), AppUserMongoEntity.class, collectionName);
        if (entity != null) {
            return Optional.of(entity);
        }
        return Optional.empty();
    }

    @Override
    public List<AppUserMongoEntity> findAllById(Iterable<String> ids) {
        return mongoTemplate.find(new Query(Criteria.where("_id")
                .in(Lists.newArrayList(ids))), AppUserMongoEntity.class, collectionName);
    }

    @Override
    public void delete(String id) {
        mongoTemplate.remove(new Query(Criteria.where("_id").is(id)), AppUserMongoEntity.class, collectionName);
    }

    void deleteAll() {
        mongoTemplate.findAllAndRemove(new Query(), AppUserMongoEntity.class, collectionName);
    }

    @Override
    public AppUserMongoEntity initNewInstance() {
        return AppUserMongoEntity.builder()
                .id(UUID.randomUUID().toString())
                .created(Instant.now())
                .capabilityIds(new HashSet<>())
                .groupIds(new HashSet<>())
                .build();
    }
}
