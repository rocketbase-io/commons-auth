package io.rocketbase.commons.service.user;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.rocketbase.commons.config.JwtProperties;
import io.rocketbase.commons.event.ActiveUserChangedEvent;
import io.rocketbase.commons.model.AppUserToken;
import org.springframework.context.ApplicationEventPublisher;

import javax.annotation.Resource;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ActiveUserStoreLocalCache implements ActiveUserStore {

    private final Cache<String, Boolean> activeUsers;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    public ActiveUserStoreLocalCache(JwtProperties jwtProperties) {
        activeUsers = CacheBuilder.newBuilder()
                .expireAfterWrite(jwtProperties.getAccessTokenExpiration(), TimeUnit.MINUTES)
                .removalListener(l -> applicationEventPublisher.publishEvent(new ActiveUserChangedEvent(ActiveUserStoreLocalCache.this)))
                .build();
    }

    protected ActiveUserStoreLocalCache(long millisToExpire) {
        activeUsers = CacheBuilder.newBuilder()
                .expireAfterWrite(millisToExpire, TimeUnit.MILLISECONDS)
                .removalListener(l -> applicationEventPublisher.publishEvent(new ActiveUserChangedEvent(ActiveUserStoreLocalCache.this)))
                .build();
    }

    @Override
    public void addUser(AppUserToken user) {
        boolean isPresent = activeUsers.getIfPresent(user.getId()) != null;
        activeUsers.put(user.getId(), true);
        if (!isPresent) {
            applicationEventPublisher.publishEvent(new ActiveUserChangedEvent(this));
        }
    }

    @Override
    public void clear() {
        activeUsers.invalidateAll();
        applicationEventPublisher.publishEvent(new ActiveUserChangedEvent(this));
    }

    @Override
    public long getUserCount() {
        activeUsers.cleanUp();
        return activeUsers.size();
    }

    @Override
    public Set<String> getUserIds() {
        activeUsers.cleanUp();
        return activeUsers.asMap().keySet();
    }
}
