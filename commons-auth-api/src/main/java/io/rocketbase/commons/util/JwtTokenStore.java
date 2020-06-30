package io.rocketbase.commons.util;

import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.exception.TokenRefreshException;

import java.io.Serializable;

public interface JwtTokenStore extends Serializable {

    boolean checkTokenNeedsRefresh();

    boolean checkTokenNeedsRefresh(long seconds);

    void refreshToken() throws TokenRefreshException;

    String getHeaderName();

    String getTokenHeader();

    JwtTokenBundle getTokenBundle();

}
