package io.rocketbase.commons.service.invite;

import com.google.common.collect.Lists;
import com.mongodb.client.result.DeleteResult;
import io.rocketbase.commons.dto.appinvite.QueryAppInvite;
import io.rocketbase.commons.model.AppInviteMongoEntity;
import io.rocketbase.commons.service.MongoQueryHelper;
import io.rocketbase.commons.util.Nulls;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class AppInviteMongoPersistenceService implements AppInvitePersistenceService<AppInviteMongoEntity>, MongoQueryHelper {

    private final MongoTemplate mongoTemplate;

    private final Snowflake snowflake;

    private final String collectionName;

    @Override
    public Page<AppInviteMongoEntity> findAll(QueryAppInvite query, Pageable pageable) {
        List<AppInviteMongoEntity> entities = mongoTemplate.find(getQuery(query).with(pageable), AppInviteMongoEntity.class, collectionName);
        long total = mongoTemplate.count(getQuery(query), AppInviteMongoEntity.class, collectionName);

        return new PageImpl<>(entities, pageable, total);
    }

    Query getQuery(QueryAppInvite query) {
        Query result = new Query();
        if (query != null) {
            if (StringUtils.hasText(query.getInvitor())) {
                result.addCriteria(buildRegexCriteria("invitor", query.getInvitor()));
            }
            if (StringUtils.hasText(query.getEmail())) {
                result.addCriteria(buildRegexCriteria("email", query.getEmail()));
            }
            if (!Nulls.notNull(query.getExpired(), false)) {
                result.addCriteria(Criteria.where("expiration").gte(Instant.now()));
            } else {
                result.addCriteria(Criteria.where("expiration").lt(Instant.now()));
            }
            if (query.getKeyValues() != null && !query.getKeyValues().isEmpty()) {
                for (Map.Entry<String, String> kv : query.getKeyValues().entrySet()) {
                    Pattern valuePattern = Pattern.compile(kv.getValue(), Pattern.CASE_INSENSITIVE);
                    result.addCriteria(Criteria.where("keyValues." + kv.getKey()).is(valuePattern));
                }
            }
        }
        return result;
    }

    @Override
    public AppInviteMongoEntity save(AppInviteMongoEntity entity) {
        if (entity.getId() == null) {
            entity.setId(snowflake.nextId());
        }
        mongoTemplate.save(entity, collectionName);
        return entity;
    }

    @Override
    public Optional<AppInviteMongoEntity> findById(Long id) {
        AppInviteMongoEntity entity = mongoTemplate.findOne(new Query(Criteria.where("_id").is(id)), AppInviteMongoEntity.class, collectionName);
        if (entity != null) {
            return Optional.of(entity);
        }
        return Optional.empty();
    }

    @Override
    public List<AppInviteMongoEntity> findAllById(Iterable<Long> ids) {
        return mongoTemplate.find(new Query(Criteria.where("_id")
                .in(Lists.newArrayList(ids))), AppInviteMongoEntity.class, collectionName);
    }

    @Override
    public void delete(Long id) {
        mongoTemplate.remove(new Query(Criteria.where("_id").is(id)), AppInviteMongoEntity.class, collectionName);
    }

    void deleteAll() {
        mongoTemplate.findAllAndRemove(new Query(), AppInviteMongoEntity.class, collectionName);
    }

    @Override
    public AppInviteMongoEntity initNewInstance() {
        return AppInviteMongoEntity.builder()
                .id(snowflake.nextId())
                .created(Instant.now())
                .capabilityIds(new HashSet<>())
                .groupIds(new HashSet<>())
                .build();
    }

    @Override
    public long deleteExpired() {
        DeleteResult remove = mongoTemplate.remove(getQuery(QueryAppInvite.builder().expired(true).build()), AppInviteMongoEntity.class, collectionName);
        return remove.getDeletedCount();
    }
}
