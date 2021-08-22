package io.rocketbase.commons;

import io.rocketbase.commons.adapters.JwtTokenProvider;
import io.rocketbase.commons.adapters.SimpleJwtTokenProvider;
import io.rocketbase.commons.converter.AppUserConverter;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.model.AppUserToken;
import io.rocketbase.commons.security.JwtTokenService;
import io.rocketbase.commons.service.user.AppUserService;
import io.rocketbase.commons.test.ModifiedJwtTokenService;
import io.rocketbase.commons.test.adapters.AuthRestTestTemplate;
import io.rocketbase.commons.test.data.*;
import io.rocketbase.commons.util.CommonsAuthCollectionNameResolver;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;
import java.util.Collection;

@ExtendWith(SpringExtension.class)
@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "test")
@Slf4j
public class BaseIntegrationTest {

    @Value("http://localhost:${local.server.port}")
    protected String baseUrl;

    @Resource
    private JwtTokenService jwtTokenService;

    @Resource
    private ModifiedJwtTokenService modifiedJwtTokenService;

    @Resource
    private AppUserService appUserService;

    @Resource
    private AppUserConverter appUserConverter;

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private CommonsAuthCollectionNameResolver collectionNameResolver;

    protected AuthRestTestTemplate getAuthRestTemplate(AppUserToken token) {
        return new AuthRestTestTemplate(token, jwtTokenService);
    }

    protected AuthRestTestTemplate getAuthRestTemplate(AppUserEntity entity) {
        return getAuthRestTemplate(toToken(entity));
    }

    protected AppUserEntity getAppUser(String username) {
        return appUserService.getByUsername(username);
    }

    protected AppUserToken toToken(AppUserEntity entity) {
        return appUserConverter.toToken(entity);
    }

    protected JwtTokenProvider getTokenProvider(String username) {
        AppUserEntity user = getAppUser(username);
        JwtTokenProvider tokenProvider = new SimpleJwtTokenProvider(getBaseUrl(), modifiedJwtTokenService.generateTokenBundle(appUserConverter.toToken(user)));
        return tokenProvider;
    }

    @BeforeEach
    public void beforeEachTest() {
        truncateAndSave(CapabilityData.getEntities(), collectionNameResolver.capability());
        truncateAndSave(ClientData.getEntities(), collectionNameResolver.client());
        truncateAndSave(GroupData.getEntities(), collectionNameResolver.group());
        truncateAndSave(TeamData.getEntities(), collectionNameResolver.team());
        truncateAndSave(InviteData.getEntities(), collectionNameResolver.invite());
        truncateAndSave(UserData.getEntities(), collectionNameResolver.user());
    }

    protected void truncateAndSave(Collection collection, String collectionName) {
        mongoTemplate.remove(new Query(), collectionName);
        for (Object o : collection) {
            mongoTemplate.save(o, collectionName);
        }
    }

    public String getBaseUrl() {
        // added prefix for testing... see: application-test.yml -> auth.prefix
        return baseUrl + "/test";
    }

    @Disabled
    @Test
    public void upAndRunning() {
        log.info("running tests with database: {}", mongoTemplate.getDb().getName());
    }
}
