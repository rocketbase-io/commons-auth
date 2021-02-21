package io.rocketbase.commons.service.capability;

import io.rocketbase.commons.dto.appcapability.QueryAppCapability;
import io.rocketbase.commons.model.AppCapabilityMongoEntity;
import io.rocketbase.commons.service.MongoQueryHelper;
import io.rocketbase.commons.util.Snowflake;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class AppCapabilityMongoPersistenceService implements AppCapabilityPersistenceService<AppCapabilityMongoEntity>, MongoQueryHelper {

    private final MongoTemplate mongoTemplate;

    private final Snowflake snowflake;

    @Override
    public Optional<AppCapabilityMongoEntity> findById(Long id) {
        AppCapabilityMongoEntity entity = mongoTemplate.findOne(new Query(Criteria.where("_id").is(id)), AppCapabilityMongoEntity.class);
        if (entity != null) {
            return Optional.of(entity);
        }
        return Optional.empty();
    }

    @Override
    public List<AppCapabilityMongoEntity> findAllById(Iterable<Long> ids) {
        return mongoTemplate.find(new Query(Criteria.where("_id")
                .in(ids)), AppCapabilityMongoEntity.class);
    }

    @Override
    public Page<AppCapabilityMongoEntity> findAll(QueryAppCapability query, Pageable pageable) {
        return null;
    }

    @Override
    public AppCapabilityMongoEntity save(AppCapabilityMongoEntity entity) {
        mongoTemplate.save(entity);
        return entity;
    }

    @Override
    public void delete(Long id) {
        mongoTemplate.remove(new Query(Criteria.where("_id").is(id)), AppCapabilityMongoEntity.class);
    }

    @Override
    public AppCapabilityMongoEntity initNewInstance() {
        return AppCapabilityMongoEntity.builder()
                .id(snowflake.nextId())
                .created(Instant.now())
                .build();
    }
}
