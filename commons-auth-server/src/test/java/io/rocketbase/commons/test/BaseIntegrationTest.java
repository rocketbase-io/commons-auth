package io.rocketbase.commons.test;


import io.rocketbase.commons.model.AppUserEntity;
import lombok.Getter;
import org.junit.Before;
import org.junit.Rule;
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
    @Rule
    public SmtpServerRule smtpServerRule = new SmtpServerRule();

    @Getter
    @Value("http://localhost:${local.server.port}")
    protected String baseUrl;

    @Resource
    private AppUserPersistenceTestService appUserPersistenceTestService;

    protected AppUserEntity getAppUser() {
        return getAppUser("user");
    }

    protected AppUserEntity getAppUser(String username) {
        return appUserPersistenceTestService.findByUsername(username).get();
    }

    @Before
    public void beforeEachTest() {
        appUserPersistenceTestService.resetData();
    }

}
