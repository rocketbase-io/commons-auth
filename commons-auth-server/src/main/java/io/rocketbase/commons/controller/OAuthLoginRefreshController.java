package io.rocketbase.commons.controller;

import io.rocketbase.commons.dto.ErrorResponse;
import io.rocketbase.commons.dto.authentication.LoginResponse;
import io.rocketbase.commons.dto.authentication.OAuthLoginResponse;
import io.rocketbase.commons.exception.BadRequestException;
import io.rocketbase.commons.model.AppUserToken;
import io.rocketbase.commons.security.JwtTokenService;
import io.rocketbase.commons.service.auth.LoginService;
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
            response.accessToken(loginResponse.getJwtTokenBundle().getToken())
                    .refreshToken(loginResponse.getJwtTokenBundle().getRefreshToken())
                    .expiresIn(getExpiresIn(loginResponse.getJwtTokenBundle().getToken()))
                    .refreshExpiresIn(getExpiresIn(loginResponse.getJwtTokenBundle().getRefreshToken()));

        } else if (oAuthRequest.getGrantType().equals(GrantType.REFRESH_TOKEN)) {
            AppUserToken appUserToken = jwtTokenService.parseToken(oAuthRequest.getRefreshToken());
            String accessToken = jwtTokenService.generateAccessToken(appUserToken);

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
                throw new BadRequestException(ErrorResponse.builder()
                        .field("grant_type", grantType == null ? "notNull" : "password|refresh_token allowed")
                        .build());
            }
            setScope(paramMap.getFirst("scope"));
            if (grantType.equals(GrantType.PASSWORD)) {
                setUsername(paramMap.getFirst("username"));
                setPassword(paramMap.getFirst("password"));
                if (username == null || password == null) {
                    ErrorResponse.ErrorResponseBuilder builder = ErrorResponse.builder();
                    if (username == null) {
                        builder.field("username", "notNull");
                    }
                    if (password == null) {
                        builder.field("password", "notNull");
                    }
                    throw new BadRequestException(builder.build());
                }
            } else if (grantType.equals(GrantType.REFRESH_TOKEN)) {
                setRefreshToken(paramMap.containsKey("refresh_token") ? paramMap.getFirst("refresh_token") : paramMap.getFirst("code"));
                if (refreshToken == null) {
                    throw new BadRequestException(ErrorResponse.builder()
                            .field("refresh_token", "notNull")
                            .build());
                }
            }
        }
    }

}
