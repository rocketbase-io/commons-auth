package org.company.sample.initializer;

import io.rocketbase.commons.dto.appcapability.AppCapabilityRead;
import io.rocketbase.commons.dto.appuser.AppUserCreate;
import io.rocketbase.commons.service.initialize.DataInitializerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Set;

@Slf4j
@Service
public class UserInitializer implements ApplicationListener<ApplicationReadyEvent> {

    @Resource
    private DataInitializerService dataInitializerService;


    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        dataInitializerService.checkUserInitialized(AppUserCreate.builder()
                .username("admin")
                .password("admin")
                .email("info@rocketbase.io")
                .capabilityIds(Set.of(AppCapabilityRead.ROOT.getId()))
                .enabled(true)
                .build());

        dataInitializerService.checkUserInitialized(AppUserCreate.builder()
                .username("user")
                .password("user")
                .email("team@rocketbase.io")
                .enabled(true)
                .build(), "api.crud", "invite");
    }
}
