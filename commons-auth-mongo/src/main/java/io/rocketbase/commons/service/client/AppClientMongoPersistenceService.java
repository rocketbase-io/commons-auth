package io.rocketbase.commons.service.client;

import io.rocketbase.commons.dto.appclient.QueryAppClient;
import io.rocketbase.commons.model.AppClientMongoEntity;
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
public class AppClientMongoPersistenceService implements AppClientPersistenceService<AppClientMongoEntity>, MongoQueryHelper {

    private final MongoTemplate mongoTemplate;

    private final Snowflake snowflake;

    @Override
    public Optional<AppClientMongoEntity> findById(Long id) {
        AppClientMongoEntity entity = mongoTemplate.findOne(new Query(Criteria.where("_id").is(id)), AppClientMongoEntity.class);
        if (entity != null) {
            return Optional.of(entity);
        }
        return Optional.empty();
    }

    @Override
    public List<AppClientMongoEntity> findAllById(Iterable<Long> ids) {
        return mongoTemplate.find(new Query(Criteria.where("_id")
                .in(ids)), AppClientMongoEntity.class);
    }

    @Override
    public Page<AppClientMongoEntity> findAll(QueryAppClient query, Pageable pageable) {

        List<AppClientMongoEntity> entities = mongoTemplate.find(getQuery(query).with(pageable), AppClientMongoEntity.class);
        long total = mongoTemplate.count(getQuery(query), AppClientMongoEntity.class);

        return new PageImpl<>(entities, pageable, total);
    }

    Query getQuery(QueryAppClient query) {
        Query result = new Query();
        if (query != null) {
            if (!StringUtils.isEmpty(query.getSystemRefId())) {
                result.addCriteria(buildRegexCriteria("systemRefId", query.getSystemRefId()));
            }
            if (!StringUtils.isEmpty(query.getName())) {
                result.addCriteria(buildRegexCriteria("name", query.getName()));
            }
            if (!StringUtils.isEmpty(query.getDescription())) {
                result.addCriteria(buildRegexCriteria("description", query.getDescription()));
            }
            if (query.getIds() != null && !query.getIds().isEmpty()) {
                result.addCriteria(Criteria.where("_id").in(query.getIds()));
            }
            if (query.getCapabilityIds() != null && !query.getCapabilityIds().isEmpty()) {
                result.addCriteria(Criteria.where("capabilityIds").in(query.getCapabilityIds()));
            }
        }
        return result;
    }

    @Override
    public AppClientMongoEntity save(AppClientMongoEntity entity) {
        if (entity.getId() == null) {
            entity.setId(snowflake.nextId());
        }
        mongoTemplate.save(entity);
        return entity;
    }

    @Override
    public void delete(Long id) {
        mongoTemplate.remove(new Query(Criteria.where("_id").is(id)), AppClientMongoEntity.class);
    }

    @Override
    public AppClientMongoEntity initNewInstance() {
        return AppClientMongoEntity.builder()
                .id(snowflake.nextId())
                .created(Instant.now())
                .build();
    }
}
