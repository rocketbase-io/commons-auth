package io.rocketbase.commons.service.capability;

import io.rocketbase.commons.dto.appcapability.QueryAppCapability;
import io.rocketbase.commons.model.AppCapabilityMongoEntity;
import io.rocketbase.commons.service.MongoQueryHelper;
import io.rocketbase.commons.util.Snowflake;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class AppCapabilityMongoPersistenceService implements AppCapabilityPersistenceService<AppCapabilityMongoEntity>, MongoQueryHelper {

    private final MongoTemplate mongoTemplate;

    private final Snowflake snowflake;

    private final String collectionName;

    @Override
    public Optional<AppCapabilityMongoEntity> findById(Long id) {
        AppCapabilityMongoEntity entity = mongoTemplate.findOne(new Query(Criteria.where("_id").is(id)), AppCapabilityMongoEntity.class, collectionName);
        if (entity != null) {
            return Optional.of(entity);
        }
        return Optional.empty();
    }

    @Override
    public List<AppCapabilityMongoEntity> findAllById(Iterable<Long> ids) {
        return mongoTemplate.find(new Query(Criteria.where("_id")
                .in(ids)), AppCapabilityMongoEntity.class, collectionName);
    }

    @Override
    public Page<AppCapabilityMongoEntity> findAll(QueryAppCapability query, Pageable pageable) {

        List<AppCapabilityMongoEntity> entities = mongoTemplate.find(getQuery(query).with(pageable), AppCapabilityMongoEntity.class, collectionName);
        long total = mongoTemplate.count(getQuery(query), AppCapabilityMongoEntity.class, collectionName);

        return new PageImpl<>(entities, pageable, total);
    }

    Query getQuery(QueryAppCapability query) {
        Query result = new Query();
        if (query != null) {

            if (!StringUtils.isEmpty(query.getKey())) {
                result.addCriteria(buildRegexCriteria("key", query.getKey()));
            }
            if (!StringUtils.isEmpty(query.getDescription())) {
                result.addCriteria(buildRegexCriteria("description", query.getDescription()));
            }
            if (!StringUtils.isEmpty(query.getKeyPath())) {
                result.addCriteria(buildRegexCriteria("keyPath", query.getKeyPath()));
            }
            if (query.getIds() != null && !query.getIds().isEmpty()) {
                result.addCriteria(Criteria.where("_id").in(query.getIds()));
            }
            if (query.getParentIds() != null && !query.getParentIds().isEmpty()) {
                result.addCriteria(Criteria.where("parentId").in(query.getParentIds()));
            }
        }
        return result;
    }

    @Override
    public AppCapabilityMongoEntity save(AppCapabilityMongoEntity entity) {
        if (entity.getId() == null) {
            entity.setId(snowflake.nextId());
        }
        mongoTemplate.save(entity, collectionName);
        return entity;
    }

    @Override
    public void delete(Long id) {
        mongoTemplate.remove(new Query(Criteria.where("_id").is(id)), AppCapabilityMongoEntity.class, collectionName);
    }

    @Override
    public AppCapabilityMongoEntity initNewInstance() {
        return AppCapabilityMongoEntity.builder()
                .id(snowflake.nextId())
                .created(Instant.now())
                .build();
    }
}
