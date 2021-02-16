package io.rocketbase.commons.controller;

import io.rocketbase.commons.dto.ErrorResponse;
import io.rocketbase.commons.dto.authentication.LoginResponse;
import io.rocketbase.commons.dto.authentication.OAuthLoginResponse;
import io.rocketbase.commons.exception.BadRequestException;
import io.rocketbase.commons.model.AppUserToken;
import io.rocketbase.commons.model.TokenParseResult;
import io.rocketbase.commons.security.JwtTokenService;
import io.rocketbase.commons.service.auth.LoginService;
import io.rocketbase.commons.service.user.ActiveUserStore;
import io.rocketbase.commons.service.user.AppUserTokenService;
import io.rocketbase.commons.util.JwtTokenDecoder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.Instant;

@Slf4j
@RestController
@RequestMapping("${auth.prefix:}")
public class OAuthLoginRefreshController {

    @Resource
    private JwtTokenService jwtTokenService;

    @Resource
    private LoginService loginService;

    @Resource
    private AppUserTokenService appUserTokenService;

    @Resource
    private ActiveUserStore activeUserStore;

    // activate cors in this way in order to work in combination with ignored security for this endpoint
    @CrossOrigin(allowedHeaders = "*", origins = "*")
    @RequestMapping(method = RequestMethod.POST, path = "/auth/oauth2/token")
    @ResponseBody
    public ResponseEntity<OAuthLoginResponse> loginOAuth(@RequestParam MultiValueMap<String, String> paramMap) {
        OAuthRequest oAuthRequest = new OAuthRequest(paramMap);

        OAuthLoginResponse.OAuthLoginResponseBuilder response = OAuthLoginResponse.builder()
                .tokenType("Bearer")
                .scope(oAuthRequest.getScope());
        if (oAuthRequest.getGrantType().equals(GrantType.PASSWORD)) {
            LoginResponse loginResponse = loginService.performLogin(oAuthRequest.getUsername(), oAuthRequest.getPassword());
            activeUserStore.addUser(loginResponse.getUser());
            response.accessToken(loginResponse.getJwtTokenBundle().getToken())
                    .refreshToken(loginResponse.getJwtTokenBundle().getRefreshToken())
                    .expiresIn(getExpiresIn(loginResponse.getJwtTokenBundle().getToken()))
                    .refreshExpiresIn(getExpiresIn(loginResponse.getJwtTokenBundle().getRefreshToken()));

        } else if (oAuthRequest.getGrantType().equals(GrantType.REFRESH_TOKEN)) {
            // validate jwt
            TokenParseResult parsedToken = jwtTokenService.parseToken(oAuthRequest.getRefreshToken());
            // lookup user with rights
            AppUserToken token = appUserTokenService.getByUsername(parsedToken.getUser().getUsername());
            // generate accessToken
            String accessToken = jwtTokenService.generateAccessToken(token);
            activeUserStore.addUser(token);
            response.accessToken(accessToken)
                    .refreshToken(oAuthRequest.getRefreshToken())
                    .expiresIn(getExpiresIn(accessToken))
                    .refreshExpiresIn(getExpiresIn(oAuthRequest.getRefreshToken()));
        }
        return ResponseEntity.ok(response.build());
    }

    protected long getExpiresIn(String token) {
        return JwtTokenDecoder.decodeTokenBody(token).getExpiration().getEpochSecond() - Instant.now().getEpochSecond();
    }

    private enum GrantType {
        PASSWORD, REFRESH_TOKEN
    }

    @Data
    private static class OAuthRequest {
        private String username;
        private String password;
        private String refreshToken;
        private GrantType grantType;
        private String scope;

        public OAuthRequest(MultiValueMap<String, String> paramMap) {
            try {
                setGrantType(GrantType.valueOf(paramMap.getFirst("grant_type").toUpperCase()));
            } catch (NullPointerException | IllegalArgumentException e) {
                throw new BadRequestException(new ErrorResponse("grant type issue")
                        .addField("grant_type", grantType == null ? "notNull" : "password|refresh_token allowed"));
            }
            setScope(paramMap.getFirst("scope"));
            if (grantType.equals(GrantType.PASSWORD)) {
                setUsername(paramMap.getFirst("username"));
                setPassword(paramMap.getFirst("password"));
                if (username == null || password == null) {
                    ErrorResponse errorResponse = new ErrorResponse("authentication is missing");
                    if (username == null) {
                        errorResponse.addField("username", "notNull");
                    }
                    if (password == null) {
                        errorResponse.addField("password", "notNull");
                    }
                    throw new BadRequestException(errorResponse);
                }
            } else if (grantType.equals(GrantType.REFRESH_TOKEN)) {
                setRefreshToken(paramMap.containsKey("refresh_token") ? paramMap.getFirst("refresh_token") : paramMap.getFirst("code"));
                if (refreshToken == null) {
                    throw new BadRequestException(new ErrorResponse("refresh token issue")
                            .addField("refresh_token", "notNull"));
                }
            }
        }
    }

}
