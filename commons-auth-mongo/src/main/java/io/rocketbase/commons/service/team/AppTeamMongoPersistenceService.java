package io.rocketbase.commons.service.team;

import io.rocketbase.commons.dto.appgroup.QueryAppGroup;
import io.rocketbase.commons.model.AppTeamMongoEntity;
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
public class AppTeamMongoPersistenceService implements AppTeamPersistenceService<AppTeamMongoEntity>, MongoQueryHelper {

    private final MongoTemplate mongoTemplate;

    private final Snowflake snowflake;

    @Override
    public Optional<AppTeamMongoEntity> findById(Long id) {
        AppTeamMongoEntity entity = mongoTemplate.findOne(new Query(Criteria.where("_id").is(id)), AppTeamMongoEntity.class);
        if (entity != null) {
            return Optional.of(entity);
        }
        return Optional.empty();
    }

    @Override
    public List<AppTeamMongoEntity> findAllById(Iterable<Long> ids) {
        return mongoTemplate.find(new Query(Criteria.where("_id")
                .in(ids)), AppTeamMongoEntity.class);
    }

    @Override
    public Page<AppTeamMongoEntity> findAll(QueryAppGroup query, Pageable pageable) {
        return null;
    }

    @Override
    public AppTeamMongoEntity save(AppTeamMongoEntity entity) {
        mongoTemplate.save(entity);
        return entity;
    }

    @Override
    public void delete(Long id) {
        mongoTemplate.remove(new Query(Criteria.where("_id").is(id)), AppTeamMongoEntity.class);
    }

    @Override
    public AppTeamMongoEntity initNewInstance() {
        return AppTeamMongoEntity.builder()
                .id(snowflake.nextId())
                .created(Instant.now())
                .build();
    }
}
