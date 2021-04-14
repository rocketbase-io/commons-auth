package io.rocketbase.commons;

import io.rocketbase.commons.adapters.JwtTokenProvider;
import io.rocketbase.commons.adapters.SimpleJwtTokenProvider;
import io.rocketbase.commons.converter.AppUserConverter;
import io.rocketbase.commons.model.*;
import io.rocketbase.commons.security.JwtTokenService;
import io.rocketbase.commons.service.user.AppUserService;
import io.rocketbase.commons.test.ModifiedJwtTokenService;
import io.rocketbase.commons.test.adapters.AuthRestTestTemplate;
import io.rocketbase.commons.test.data.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;
import java.util.Collection;

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
        truncateAndSave(CapabilityData.getEntities(), AppCapabilityMongoEntity.COLLECTION_NAME);
        truncateAndSave(ClientData.getEntities(), AppClientMongoEntity.COLLECTION_NAME);
        truncateAndSave(GroupData.getEntities(), AppGroupMongoEntity.COLLECTION_NAME);
        truncateAndSave(TeamData.getEntities(), AppTeamMongoEntity.COLLECTION_NAME);
        truncateAndSave(InviteData.getEntities(), AppInviteMongoEntity.COLLECTION_NAME);
        truncateAndSave(UserData.getEntities(), AppUserMongoEntity.COLLECTION_NAME);
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
