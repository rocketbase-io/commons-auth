package io.rocketbase.commons.service;

import com.mongodb.client.result.DeleteResult;
import io.rocketbase.commons.dto.appinvite.QueryAppInvite;
import io.rocketbase.commons.model.AppInviteMongoEntity;
import io.rocketbase.commons.service.invite.AppInvitePersistenceService;
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
public class AppInviteMongoServiceImpl implements AppInvitePersistenceService<AppInviteMongoEntity> {

    private final MongoTemplate mongoTemplate;

    private final Snowflake snowflake;

    @Override
    public Page<AppInviteMongoEntity> findAll(QueryAppInvite query, Pageable pageable) {
        List<AppInviteMongoEntity> entities = mongoTemplate.find(getQuery(query).with(pageable), AppInviteMongoEntity.class);
        long total = mongoTemplate.count(getQuery(query), AppInviteMongoEntity.class);

        return new PageImpl<>(entities, pageable, total);
    }

    Query getQuery(QueryAppInvite query) {
        Query result = new Query();
        if (query != null) {
            if (!StringUtils.isEmpty(query.getInvitor())) {
                result.addCriteria(buildRegexCriteria("invitor", query.getInvitor()));
            }
            if (!StringUtils.isEmpty(query.getEmail())) {
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
                    result.addCriteria(Criteria.where("keyValueMap." + kv.getKey()).is(valuePattern));
                }
            }
        }
        return result;
    }

    Criteria buildRegexCriteria(String where, String text) {
        String pattern = text.trim() + "";
        if (!pattern.contains(".*")) {
            pattern = ".*" + pattern + ".*";
        }
        return Criteria.where(where).regex(pattern, "i");
    }

    @Override
    public AppInviteMongoEntity save(AppInviteMongoEntity entity) {
        mongoTemplate.save(entity);
        return entity;
    }

    @Override
    public Optional<AppInviteMongoEntity> findById(String id) {
        AppInviteMongoEntity entity = mongoTemplate.findOne(new Query(Criteria.where("_id")
                .is(id)), AppInviteMongoEntity.class);
        if (entity != null) {
            return Optional.of(entity);
        }
        return Optional.empty();
    }

    @Override
    public long count() {
        return mongoTemplate.count(new Query(), AppInviteMongoEntity.class);
    }

    @Override
    public void delete(AppInviteMongoEntity entity) {
        mongoTemplate.remove(new Query(Criteria.where("_id")
                .is(entity.getId())), AppInviteMongoEntity.class);
    }

    @Override
    public void deleteAll() {
        mongoTemplate.findAllAndRemove(new Query(), AppInviteMongoEntity.class);
    }

    @Override
    public AppInviteMongoEntity initNewInstance() {
        return AppInviteMongoEntity.builder()
                .id(snowflake.nextId())
                .created(Instant.now())
                .capabilities(new HashSet<>())
                .groups(new HashSet<>())
                .build();
    }

    @Override
    public long deleteExpired() {
        DeleteResult remove = mongoTemplate.remove(getQuery(QueryAppInvite.builder().expired(true).build()), AppInviteMongoEntity.class);
        return remove.getDeletedCount();
    }
}
