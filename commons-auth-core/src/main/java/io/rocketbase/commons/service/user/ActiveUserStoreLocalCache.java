package io.rocketbase.commons.service.user;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.rocketbase.commons.config.JwtProperties;
import io.rocketbase.commons.model.AppUserToken;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ActiveUserStoreLocalCache implements ActiveUserStore {

    private final Cache<String, String> activeUsers;

    public ActiveUserStoreLocalCache(JwtProperties jwtProperties) {
        activeUsers = CacheBuilder.newBuilder()
                .expireAfterWrite(jwtProperties.getAccessTokenExpiration(), TimeUnit.MINUTES)
                .build();
    }

    protected ActiveUserStoreLocalCache(long millisToExpire) {
        activeUsers = CacheBuilder.newBuilder()
                .expireAfterWrite(millisToExpire, TimeUnit.MILLISECONDS)
                .build();
    }

    @Override
    public void addUser(AppUserToken user) {
        activeUsers.put(user.getId(), user.getUsername());
    }

    @Override
    public void clear() {
        activeUsers.invalidateAll();
    }

    @Override
    public long getUserCount() {
        activeUsers.cleanUp();
        return activeUsers.size();
    }

    @Override
    public Set<String> getUserIds() {
        return activeUsers.asMap().keySet();
    }
}
