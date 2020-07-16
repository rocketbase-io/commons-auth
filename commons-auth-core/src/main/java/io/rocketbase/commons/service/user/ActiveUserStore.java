package io.rocketbase.commons.service.user;

import io.rocketbase.commons.model.AppUserToken;

import java.util.Set;

public interface ActiveUserStore {
    void addUser(AppUserToken user);

    void clear();

    long getUserCount();

    Set<String> getUserIds();
}
