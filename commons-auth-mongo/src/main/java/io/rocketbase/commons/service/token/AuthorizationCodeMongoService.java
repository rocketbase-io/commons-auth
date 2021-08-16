package io.rocketbase.commons.service.token;

import io.rocketbase.commons.model.AuthorizationCodeMongoEntity;
import io.rocketbase.commons.service.MongoQueryHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
public class AuthorizationCodeMongoService implements AuthorizationCodeService, MongoQueryHelper {

    private final MongoTemplate mongoTemplate;

    private final String collectionName;

    @Override
    public Optional<AuthorizationCode> findByCode(String code) {
        AuthorizationCodeMongoEntity entity = mongoTemplate.findOne(new Query(Criteria.where("code").is(code)), AuthorizationCodeMongoEntity.class, collectionName);
        if (entity != null) {
            return Optional.of(entity.toDto());
        }
        return Optional.empty();
    }

    @Override
    public AuthorizationCode save(AuthorizationCode code) {
        AuthorizationCodeMongoEntity entity = mongoTemplate.save(new AuthorizationCodeMongoEntity(code), collectionName);
        return entity.toDto();
    }

    @Override
    public void delete(String code) {
        mongoTemplate.remove(new Query(Criteria.where("code").is(code)), AuthorizationCodeMongoEntity.class, collectionName);
    }

    @Override
    public void deleteByUserId(String userId) {
        mongoTemplate.remove(new Query(Criteria.where("userId").is(userId)), AuthorizationCodeMongoEntity.class, collectionName);
    }

    @Override
    public void deleteInvalid() {
        mongoTemplate.remove(new Query(Criteria.where("invalid").lt(Instant.now())), AuthorizationCodeMongoEntity.class, collectionName);
    }
}
