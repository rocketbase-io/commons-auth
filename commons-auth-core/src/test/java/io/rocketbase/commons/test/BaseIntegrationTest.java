package io.rocketbase.commons.test;


import io.rocketbase.commons.model.AppUser;
import io.rocketbase.commons.service.AppUserPersistenceService;
import lombok.Getter;
import org.junit.Rule;
import org.junit.runner.RunWith;
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

    @Resource
    private AppUserPersistenceService appUserPersistenceService;

    protected AppUser buildSampleUser() {
        return (AppUser) appUserPersistenceService.findByUsername("user").get();
    }

    protected AppUser buildSampleAdmin() {
        return (AppUser) appUserPersistenceService.findByUsername("admin").get();
    }


}
