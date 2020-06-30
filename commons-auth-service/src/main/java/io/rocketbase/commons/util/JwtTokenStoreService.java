package io.rocketbase.commons.util;

import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.exception.TokenRefreshException;
import io.rocketbase.commons.model.AppUserToken;
import io.rocketbase.commons.security.JwtTokenService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtTokenStoreService extends AbstractJwtTokenStore {

    private final JwtTokenService jwtTokenService;

    public JwtTokenStoreService(JwtTokenBundle tokenBundle, JwtTokenService jwtTokenService) {
        super(tokenBundle);
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public void refreshToken() throws TokenRefreshException {
        try {
            AppUserToken token = jwtTokenService.parseToken(getTokenBundle().getRefreshToken());
            tokenBundle.setToken(jwtTokenService.generateAccessToken(token));

            lastToken = null;
            exp = null;

            if (log.isTraceEnabled()) {
                log.trace("refreshed token before processing http-request");
            }
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("couldn't refresh token. got error: {}", e.getMessage());
            }
            throw new TokenRefreshException();
        }
    }
}
