package io.rocketbase.commons.util;

import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.exception.TokenRefreshException;
import io.rocketbase.commons.model.AppUserToken;
import io.rocketbase.commons.model.TokenParseResult;
import io.rocketbase.commons.security.JwtTokenService;
import io.rocketbase.commons.service.user.AppUserTokenService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtTokenStoreService extends AbstractJwtTokenStore {

    private final JwtTokenService jwtTokenService;
    private final AppUserTokenService appUserTokenService;

    public JwtTokenStoreService(JwtTokenBundle tokenBundle, JwtTokenService jwtTokenService, AppUserTokenService appUserTokenService) {
        super(tokenBundle);
        this.jwtTokenService = jwtTokenService;
        this.appUserTokenService = appUserTokenService;
    }

    @Override
    public void refreshToken() throws TokenRefreshException {
        try {
            TokenParseResult parsedToken = jwtTokenService.parseToken(getTokenBundle().getRefreshToken());
            AppUserToken appUserToken = appUserTokenService.findByUsername(parsedToken.getUser().getUsername()).orElseThrow(NotFoundException::new);

            tokenBundle.setToken(jwtTokenService.generateAccessToken(appUserToken));

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
