package io.rocketbase.commons.service.group;

import io.rocketbase.commons.dto.appgroup.QueryAppGroup;
import io.rocketbase.commons.model.AppGroupMongoEntity;
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
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class AppGroupMongoPersistenceService implements AppGroupPersistenceService<AppGroupMongoEntity>, MongoQueryHelper {

    private final MongoTemplate mongoTemplate;

    private final Snowflake snowflake;

    private final String collectionName;

    @Override
    public Optional<AppGroupMongoEntity> findById(Long id) {
        AppGroupMongoEntity entity = mongoTemplate.findOne(new Query(Criteria.where("_id").is(id)), AppGroupMongoEntity.class, collectionName);
        if (entity != null) {
            return Optional.of(entity);
        }
        return Optional.empty();
    }

    @Override
    public List<AppGroupMongoEntity> findAllById(Iterable<Long> ids) {
        return mongoTemplate.find(new Query(Criteria.where("_id")
                .in(ids)), AppGroupMongoEntity.class, collectionName);
    }

    @Override
    public Page<AppGroupMongoEntity> findAll(QueryAppGroup query, Pageable pageable) {

        List<AppGroupMongoEntity> entities = mongoTemplate.find(getQuery(query).with(pageable), AppGroupMongoEntity.class, collectionName);
        long total = mongoTemplate.count(getQuery(query), AppGroupMongoEntity.class, collectionName);

        return new PageImpl<>(entities, pageable, total);
    }

    Query getQuery(QueryAppGroup query) {
        Query result = new Query();
        if (query != null) {
            if (!StringUtils.isEmpty(query.getNamePath())) {
                result.addCriteria(buildRegexCriteria("namePath", query.getNamePath()));
            }
            if (!StringUtils.isEmpty(query.getSystemRefId())) {
                result.addCriteria(buildRegexCriteria("systemRefId", query.getSystemRefId()));
            }
            if (!StringUtils.isEmpty(query.getName())) {
                result.addCriteria(buildRegexCriteria("name", query.getName()));
            }
            if (query.getParentIds() != null && !query.getParentIds().isEmpty()) {
                result.addCriteria(Criteria.where("parentId").in(query.getParentIds()));
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
            if (query.getKeyValues() != null && !query.getKeyValues().isEmpty()) {
                for (Map.Entry<String, String> kv : query.getKeyValues().entrySet()) {
                    Pattern valuePattern = Pattern.compile(kv.getValue(), Pattern.CASE_INSENSITIVE);
                    result.addCriteria(Criteria.where("keyValueMap." + kv.getKey()).is(valuePattern));
                }
            }
        }
        return result;
    }

    @Override
    public AppGroupMongoEntity save(AppGroupMongoEntity entity) {
        if (entity.getId() == null) {
            entity.setId(snowflake.nextId());
        }
        mongoTemplate.save(entity, collectionName);
        return entity;
    }

    @Override
    public void delete(Long id) {
        mongoTemplate.remove(new Query(Criteria.where("_id").is(id)), AppGroupMongoEntity.class, collectionName);
    }

    @Override
    public AppGroupMongoEntity initNewInstance() {
        return AppGroupMongoEntity.builder()
                .id(snowflake.nextId())
                .created(Instant.now())
                .build();
    }
}
