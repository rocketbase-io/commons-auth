package io.rocketbase.commons.service.initialize;

import com.google.common.collect.Sets;
import io.rocketbase.commons.dto.appcapability.AppCapabilityRead;
import io.rocketbase.commons.dto.appcapability.AppCapabilityWrite;
import io.rocketbase.commons.dto.appcapability.QueryAppCapability;
import io.rocketbase.commons.dto.appclient.AppClientWrite;
import io.rocketbase.commons.dto.appclient.QueryAppClient;
import io.rocketbase.commons.dto.appuser.AppUserCreate;
import io.rocketbase.commons.model.AppCapabilityEntity;
import io.rocketbase.commons.model.AppClientEntity;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.service.capability.AppCapabilityService;
import io.rocketbase.commons.service.client.AppClientService;
import io.rocketbase.commons.service.user.AppUserService;
import io.rocketbase.commons.util.Nulls;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class DefaultDataInitializerServiceService implements DataInitializerService {

    private final AppUserService appUserService;
    private final AppCapabilityService appCapabilityService;
    private final AppClientService appClientService;

    @Override
    public AppUserEntity checkUserInitialized(AppUserCreate create, String... keyPaths) {
        AppUserEntity user = appUserService.getByUsername(create.getUsername());
        if (user == null) {
            if (keyPaths != null) {
                Set<Long> capabilityIds = new HashSet<>(Nulls.notNull(create.getCapabilityIds()));
                capabilityIds.addAll(keyPathToIds(keyPaths));
                create.setCapabilityIds(capabilityIds);
            }
            user = appUserService.initializeUser(create);
            log.info("initialized {}", create.getUsername());
        }
        return user;
    }

    private Set<Long> keyPathToIds(String[] keyPaths) {
        Set<Long> result = new HashSet<>();
        for (String keyPath : keyPaths) {
            AppCapabilityEntity capabilityEntity = checkCapabilityKeyPathInitialized(keyPath);
            result.add(capabilityEntity.getId());
        }
        return result;
    }

    @Override
    public AppCapabilityEntity checkCapabilityKeyPathInitialized(String keyPath) {
        if (!StringUtils.hasText(keyPath)) {
            return null;
        }
        if (keyPath.equals(AppCapabilityRead.ROOT.getKeyPath())) {
            return appCapabilityService.findById(AppCapabilityRead.ROOT.getId()).get();
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

    @Override
    public AppClientEntity checkClient(String name, Set<String> redirectUrls, String... keyPaths) {
        Assert.notEmpty(redirectUrls, "redirect urls needs to have at least one entry");
        Page<AppClientEntity> result = appClientService.findAll(QueryAppClient.builder()
                .name(name)
                .redirectUrl(redirectUrls.iterator().next())
                .build(), PageRequest.of(0, 100));
        Optional<AppClientEntity> optionalFound = result.getContent().stream().filter(v -> v.getRedirectUrls().equals(redirectUrls)).sorted(Comparator.comparing(AppClientEntity::getId)).findFirst();
        if (optionalFound.isPresent()) {
            return optionalFound.get();
        }
        return appClientService.create(AppClientWrite.builder()
                .name(name)
                .redirectUrls(redirectUrls)
                .capabilityIds(keyPathToIds(keyPaths))
                .build());
    }


}
