package io.rocketbase.commons.service.initialize;

import com.google.common.collect.Sets;
import io.rocketbase.commons.dto.appcapability.AppCapabilityRead;
import io.rocketbase.commons.dto.appcapability.AppCapabilityWrite;
import io.rocketbase.commons.dto.appcapability.QueryAppCapability;
import io.rocketbase.commons.dto.appuser.AppUserCreate;
import io.rocketbase.commons.model.AppCapabilityEntity;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.service.capability.AppCapabilityService;
import io.rocketbase.commons.service.user.AppUserService;
import io.rocketbase.commons.util.Nulls;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StringUtils;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class DefaultDataInitializerServiceService implements DataInitializerService {

    private final AppUserService appUserService;
    private final AppCapabilityService appCapabilityService;

    @Override
    public AppUserEntity checkUserInitialized(AppUserCreate create, String... keyPaths) {
        AppUserEntity user = appUserService.getByUsername(create.getUsername());
        if (user == null) {
            if (keyPaths != null) {
                Set<Long> capabilityIds = new HashSet<>(Nulls.notNull(create.getCapabilityIds()));
                for (String keyPath : keyPaths) {
                    AppCapabilityEntity capabilityEntity = checkCapabilityKeyPathInitialized(keyPath);
                    capabilityIds.add(capabilityEntity.getId());
                }
                create.setCapabilityIds(capabilityIds);
            }
            user = appUserService.initializeUser(create);
            log.info("initialized {}", create.getUsername());
        }
        return user;
    }

    @Override
    public AppCapabilityEntity checkCapabilityKeyPathInitialized(String keyPath) {
        if (!StringUtils.hasText(keyPath)) {
            return null;
        }
        List<String> pathElements = new ArrayList<>();
        if (keyPath.contains(".")) {
            String[] strings = keyPath.split("\\.");
            pathElements.addAll(Arrays.asList(strings));
        } else {
            pathElements.add(keyPath);
        }
        AppCapabilityEntity entity = null;
        for (String key : pathElements) {
            Long parentId = entity == null ? AppCapabilityRead.ROOT.getId() : entity.getId();
            Page<AppCapabilityEntity> response = appCapabilityService.findAll(QueryAppCapability.builder()
                    .parentIds(Sets.newHashSet(parentId))
                    .key(key)
                    .build(), PageRequest.of(0, 1));
            if (response.isEmpty()) {
                entity = appCapabilityService.create(AppCapabilityWrite.builder()
                        .key(key)
                        .build(), parentId);
            } else {
                entity = response.getContent().get(0);
            }
        }
        return entity;
    }
}
