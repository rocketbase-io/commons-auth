package io.rocketbase.commons.test;


import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.model.AppUserToken;
import io.rocketbase.commons.security.JwtTokenService;
import io.rocketbase.commons.test.adapters.AuthRestTestTemplate;
import lombok.Getter;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "test")
public abstract class BaseIntegrationTest {

    @Getter
    @Value("http://localhost:${local.server.port}")
    protected String baseUrl;

    @Resource
    private JwtTokenService jwtTokenService;

    protected AuthRestTestTemplate getAuthRestTemplate(AppUserToken token) {
        return new AuthRestTestTemplate(token, jwtTokenService);
    }

    protected AppUserEntity getAppUser(String username) {
        // TODO: need implementation
        return null;
    }

    @Before
    public void beforeEachTest() {
        // appUserPersistenceTestService.resetData();
    }

}
