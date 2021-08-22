package io.rocketbase.commons.config;

import io.rocketbase.commons.model.*;
import io.rocketbase.commons.util.CommonsAuthCollectionNameResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexDefinition;

import javax.annotation.Resource;

@Slf4j
@Configuration
@AutoConfigureBefore(AuthMongoAutoConfiguration.class)
public class AuthMongoCollectionAutoConfiguration implements ApplicationListener<ApplicationStartedEvent> {

    @Resource
    private MongoTemplate mongoTemplate;

    @Bean
    public CommonsAuthCollectionNameResolver commonsAuthCollectionNameResolver(@Autowired ConfigurableBeanFactory configurableBeanFactory) {
        return new CommonsAuthCollectionNameResolver(configurableBeanFactory);
    }


    protected void executeIndexCheck(Class<?> entityClass, IndexDefinition indexDefinition) {
        try {
            mongoTemplate
                    .indexOps(entityClass)
                    .ensureIndex(indexDefinition);
        } catch (Exception e) {
            log.error("indexing {} got a problem: {}", entityClass.getSimpleName(), e.getMessage());
        }
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
        capabilityIndices();
        clientIndices();
        groupIndices();
        inviteIndices();
        teamIndices();
        userIndices();
        authcodeIndices();
    }

    private void capabilityIndices() {
        executeIndexCheck(AppCapabilityEntity.class,
                new Index()
                        .named("capability_key")
                        .on("key", Sort.Direction.ASC));
        executeIndexCheck(AppCapabilityEntity.class,
                new Index()
                        .named("capability_parentId")
                        .on("parentId", Sort.Direction.ASC));
        executeIndexCheck(AppCapabilityEntity.class,
                new Index()
                        .named("capability_systemRefId")
                        .on("systemRefId", Sort.Direction.ASC));
    }

    private void clientIndices() {
        executeIndexCheck(AppClientMongoEntity.class,
                new Index()
                        .named("client_redirectUrls")
                        .on("redirectUrls", Sort.Direction.ASC));
        executeIndexCheck(AppClientMongoEntity.class,
                new Index()
                        .named("client_systemRefId")
                        .on("systemRefId", Sort.Direction.ASC));
    }

    private void groupIndices() {
        executeIndexCheck(AppGroupMongoEntity.class,
                new Index()
                        .named("group_parentId")
                        .on("parentId", Sort.Direction.ASC));
        executeIndexCheck(AppGroupMongoEntity.class,
                new Index()
                        .named("group_systemRefId")
                        .on("systemRefId", Sort.Direction.ASC));
    }

    private void inviteIndices() {
        executeIndexCheck(AppInviteEntity.class,
                new Index()
                        .named("invite_expiration")
                        .on("expiration", Sort.Direction.DESC));
        executeIndexCheck(AppInviteEntity.class,
                new Index()
                        .named("invite_systemRefId")
                        .on("systemRefId", Sort.Direction.ASC));
    }

    private void teamIndices() {
        executeIndexCheck(AppTeamMongoEntity.class,
                new Index()
                        .named("team_expiration")
                        .on("members", Sort.Direction.ASC));
        executeIndexCheck(AppTeamMongoEntity.class,
                new Index()
                        .named("team_systemRefId")
                        .on("systemRefId", Sort.Direction.ASC));
    }

    private void userIndices() {
        executeIndexCheck(AppUserMongoEntity.class,
                new Index()
                        .named("user_username")
                        .on("username", Sort.Direction.ASC)
                        .unique());
        executeIndexCheck(AppUserMongoEntity.class,
                new Index()
                        .named("user_email")
                        .on("email", Sort.Direction.ASC)
                        .unique());
        executeIndexCheck(AppUserMongoEntity.class,
                new Index()
                        .named("user_capabilityIds")
                        .on("capabilityIds", Sort.Direction.ASC));
        executeIndexCheck(AppUserMongoEntity.class,
                new Index()
                        .named("user_groupIds")
                        .on("groupIds", Sort.Direction.ASC));
        executeIndexCheck(AppUserMongoEntity.class,
                new Index()
                        .named("user_systemRefId")
                        .on("systemRefId", Sort.Direction.ASC));
    }

    private void authcodeIndices() {
        executeIndexCheck(AuthorizationCodeMongoEntity.class,
                new Index()
                        .named("authcode_userId")
                        .on("userId", Sort.Direction.ASC));
        executeIndexCheck(AuthorizationCodeMongoEntity.class,
                new Index()
                        .named("authcode_invalid")
                        .on("invalid", Sort.Direction.DESC));
    }
}
