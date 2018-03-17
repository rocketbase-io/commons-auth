package io.rocketbase.sample.initializer;

import io.rocketbase.commons.service.AppUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Slf4j
@Service
public class UserInitializer {

    @Resource
    private AppUserService appUserService;

    @PostConstruct
    public void postConstruct() {
        if (appUserService.getByUsername("admin") == null) {
            appUserService.initializeUser("admin", "admin", "info@rocketbase.io", true);
        }
    }
}
