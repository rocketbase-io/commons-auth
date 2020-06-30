package io.rocketbase.commons.service;

import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.util.JwtTokenStore;

public interface JwtTokenStoreProvider {

    JwtTokenStore getInstance(JwtTokenBundle tokenBundle);
}
