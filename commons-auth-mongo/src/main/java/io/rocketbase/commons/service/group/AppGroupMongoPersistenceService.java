package io.rocketbase.commons.service.group;

import io.rocketbase.commons.dto.appgroup.QueryAppGroup;
import io.rocketbase.commons.model.AppGroupMongoEntity;
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
public class AppGroupMongoPersistenceService implements AppGroupPersistenceService<AppGroupMongoEntity>, MongoQueryHelper {

    private final MongoTemplate mongoTemplate;

    private final Snowflake snowflake;

    @Override
    public Optional<AppGroupMongoEntity> findById(Long id) {
        AppGroupMongoEntity entity = mongoTemplate.findOne(new Query(Criteria.where("_id").is(id)), AppGroupMongoEntity.class);
        if (entity != null) {
            return Optional.of(entity);
        }
        return Optional.empty();
    }

    @Override
    public List<AppGroupMongoEntity> findAllById(Iterable<Long> ids) {
        return mongoTemplate.find(new Query(Criteria.where("_id")
                .in(ids)), AppGroupMongoEntity.class);
    }

    @Override
    public Page<AppGroupMongoEntity> findAll(QueryAppGroup query, Pageable pageable) {
        return null;
    }

    @Override
    public AppGroupMongoEntity save(AppGroupMongoEntity entity) {
        mongoTemplate.save(entity);
        return entity;
    }

    @Override
    public void delete(Long id) {
        mongoTemplate.remove(new Query(Criteria.where("_id").is(id)), AppGroupMongoEntity.class);
    }

    @Override
    public AppGroupMongoEntity initNewInstance() {
        return AppGroupMongoEntity.builder()
                .id(snowflake.nextId())
                .created(Instant.now())
                .build();
    }
}
