package io.rocketbase.commons.service.team;

import com.google.common.collect.Lists;
import io.rocketbase.commons.dto.appteam.QueryAppTeam;
import io.rocketbase.commons.model.AppTeamMongoEntity;
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
public class AppTeamMongoPersistenceService implements AppTeamPersistenceService<AppTeamMongoEntity>, MongoQueryHelper {

    private final MongoTemplate mongoTemplate;

    private final Snowflake snowflake;

    private final String collectionName;

    @Override
    public Optional<AppTeamMongoEntity> findById(Long id) {
        AppTeamMongoEntity entity = mongoTemplate.findOne(new Query(Criteria.where("_id").is(id)), AppTeamMongoEntity.class, collectionName);
        if (entity != null) {
            return Optional.of(entity);
        }
        return Optional.empty();
    }

    @Override
    public List<AppTeamMongoEntity> findAllById(Iterable<Long> ids) {
        return mongoTemplate.find(new Query(Criteria.where("_id")
                .in(Lists.newArrayList(ids))), AppTeamMongoEntity.class, collectionName);
    }

    @Override
    public Page<AppTeamMongoEntity> findAll(QueryAppTeam query, Pageable pageable) {

        List<AppTeamMongoEntity> entities = mongoTemplate.find(getQuery(query).with(pageable), AppTeamMongoEntity.class, collectionName);
        long total = mongoTemplate.count(getQuery(query), AppTeamMongoEntity.class, collectionName);

        return new PageImpl<>(entities, pageable, total);
    }

    Query getQuery(QueryAppTeam query) {
        Query result = new Query();
        if (query != null) {
            if (StringUtils.hasText(query.getName())) {
                result.addCriteria(buildRegexCriteria("name", query.getName()));
            }
            if (StringUtils.hasText(query.getDescription())) {
                result.addCriteria(buildRegexCriteria("description", query.getDescription()));
            }
            if (query.getIds() != null && !query.getIds().isEmpty()) {
                result.addCriteria(Criteria.where("_id").in(query.getIds()));
            }
            if (query.getPersonal() != null) {
                result.addCriteria(Criteria.where("personal").is(query.getPersonal()));
            }
        }
        return result;
    }

    @Override
    public AppTeamMongoEntity save(AppTeamMongoEntity entity) {
        if (entity.getId() == null) {
            entity.setId(snowflake.nextId());
        }
        mongoTemplate.save(entity, collectionName);
        return entity;
    }

    @Override
    public void delete(Long id) {
        mongoTemplate.remove(new Query(Criteria.where("_id").is(id)), AppTeamMongoEntity.class, collectionName);
    }

    @Override
    public AppTeamMongoEntity initNewInstance() {
        return AppTeamMongoEntity.builder()
                .id(snowflake.nextId())
                .created(Instant.now())
                .build();
    }
}
