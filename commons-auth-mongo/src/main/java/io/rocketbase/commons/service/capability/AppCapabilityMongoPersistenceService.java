package io.rocketbase.commons.service.capability;

import com.google.common.collect.Lists;
import io.rocketbase.commons.dto.appcapability.AppCapabilityRead;
import io.rocketbase.commons.dto.appcapability.QueryAppCapability;
import io.rocketbase.commons.exception.BadRequestException;
import io.rocketbase.commons.model.AppCapabilityMongoEntity;
import io.rocketbase.commons.service.MongoQueryHelper;
import io.rocketbase.commons.util.Snowflake;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static io.rocketbase.commons.dto.appcapability.AppCapabilityRead.ROOT;

@Slf4j
@RequiredArgsConstructor
public class AppCapabilityMongoPersistenceService implements AppCapabilityPersistenceService<AppCapabilityMongoEntity>, MongoQueryHelper, InitializingBean {

    private final MongoTemplate mongoTemplate;
    private final Snowflake snowflake;
    private final String collectionName;
    private final boolean initializeRoot;
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
                .in(Lists.newArrayList(ids))), AppCapabilityMongoEntity.class, collectionName);
    }

    @Override
    public List<AppCapabilityMongoEntity> findAllByParentId(Iterable<Long> ids) {
        return mongoTemplate.find(
                new Query(Criteria.where("parentId").in(Lists.newArrayList(ids)))
                        .addCriteria(Criteria.where("_id").ne(AppCapabilityRead.ROOT.getId())), AppCapabilityMongoEntity.class, collectionName);
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
            if (StringUtils.hasText(query.getDescription())) {
                result.addCriteria(buildRegexCriteria("description", query.getDescription()));
            }
            if (StringUtils.hasText(query.getKeyPath())) {
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
        if (AppCapabilityRead.ROOT.getId().equals(id)) {
            throw new BadRequestException("root is not deletable!");
        }
        Set<AppCapabilityMongoEntity> resolved = resolveTree(Arrays.asList(id));
        mongoTemplate.remove(new Query(Criteria.where("_id").in(resolved.stream().map(AppCapabilityMongoEntity::getId).collect(Collectors.toList()))), AppCapabilityMongoEntity.class, collectionName);
    }

    @Override
    public AppCapabilityMongoEntity initNewInstance() {
        return AppCapabilityMongoEntity.builder()
                .id(snowflake.nextId())
                .created(Instant.now())
                .build();
    }

    @Override
    public void ensureInitializedRoot() {
        if (findAllById(Arrays.asList(ROOT.getId())).isEmpty()) {
            save(AppCapabilityMongoEntity.builder()
                    .id(ROOT.getId())
                    .systemRefId(ROOT.getSystemRefId())
                    .key(ROOT.getKey())
                    .description(ROOT.getDescription())
                    .parentId(ROOT.getParentId())
                    .keyPath(ROOT.getKeyPath())
                    .withChildren(false)
                    .created(ROOT.getCreated())
                    .modified(Instant.now())
                    .modifiedBy(ROOT.getModifiedBy())
                    .build());
            log.debug("root capability persisted");
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (initializeRoot) {
            ensureInitializedRoot();
        }
    }
}
