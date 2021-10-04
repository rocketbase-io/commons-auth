package io.rocketbase.commons.controller;

import io.jsonwebtoken.JwtException;
import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.dto.authentication.LoginResponse;
import io.rocketbase.commons.dto.authentication.oauth.AuthRequest;
import io.rocketbase.commons.dto.authentication.oauth.TokenRequest;
import io.rocketbase.commons.dto.authentication.oauth.TokenResponse;
import io.rocketbase.commons.dto.openid.WellKnownConfiguration;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.exception.OAuthException;
import io.rocketbase.commons.filter.LoginCookieFilter;
import io.rocketbase.commons.handler.LoginSuccessCookieHandler;
import io.rocketbase.commons.model.AppClientEntity;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.model.AppUserToken;
import io.rocketbase.commons.model.TokenParseResult;
import io.rocketbase.commons.security.CommonsAuthenticationToken;
import io.rocketbase.commons.security.JwtTokenService;
import io.rocketbase.commons.service.auth.LoginService;
import io.rocketbase.commons.service.capability.AppCapabilityService;
import io.rocketbase.commons.service.client.AppClientService;
import io.rocketbase.commons.service.token.AuthorizationCode;
import io.rocketbase.commons.service.token.AuthorizationCodeService;
import io.rocketbase.commons.service.user.ActiveUserStore;
import io.rocketbase.commons.service.user.AppUserService;
import io.rocketbase.commons.service.user.AppUserTokenService;
import io.rocketbase.commons.util.JwtTokenDecoder;
import io.rocketbase.commons.util.Nulls;
import io.rocketbase.commons.util.UrlParts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.WebUtils;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("${auth.prefix:}")
public class OAuthController {


    @Resource
    private AuthorizationCodeService authorizationCodePersistenceService;

    @Resource
    private JwtTokenService jwtTokenService;

    @Resource
    private LoginService loginService;

    @Resource
    private AppUserTokenService appUserTokenService;

    @Resource
    private ActiveUserStore activeUserStore;

    @Resource
    private AppUserService appUserService;

    @Resource
    private AppClientService appClientService;

    @Resource
    private AppCapabilityService appCapabilityService;

    // activate cors in this way in order to work in combination with ignored security for this endpoint
    @CrossOrigin(allowedHeaders = "*", origins = "*")
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, path = "/oauth/auth")
    @ResponseBody
    public RedirectView auth(@Validated AuthRequest authRequest, HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try {
            String userId = verifyUserId(request, response, authentication);
            AuthorizationCode code = new AuthorizationCode(authRequest, userId, 300);
            authorizationCodePersistenceService.save(code);

            if (userId == null) {
                Cookie cookie = new Cookie(LoginSuccessCookieHandler.OAUTH_REDIRECT, code.getCode());
                cookie.setMaxAge(300);
                cookie.setHttpOnly(false);
                cookie.setPath("/");
                response.addCookie(cookie);
                return new RedirectView("/login", true);
            }
            response.sendRedirect(code.buildRedirectUri());
        }catch (Exception e) {
            log.error("processing error", e);
        }
        return null;
    }

    private String verifyUserId(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication != null && CommonsAuthenticationToken.class.isAssignableFrom(authentication.getClass())) {
            return ((CommonsAuthenticationToken) authentication).getId();
        } else {
            Cookie authRemember = WebUtils.getCookie(request, LoginSuccessCookieHandler.AUTH_REMEMBER);
            if (authRemember != null && StringUtils.hasText(authRemember.getValue())) {
                try {
                    TokenParseResult parsedToken = jwtTokenService.parseToken(authRemember.getValue());
                    AppUserEntity userEntity = appUserService.findByIdOrUsername(parsedToken.getUser().getUsername()).orElseThrow(NotFoundException::new);
                    if (!jwtTokenService.validateToken(parsedToken, userEntity.getUsername(), userEntity.getLastTokenInvalidation())) {
                        throw new JwtException("token is issued before lastTokenInvalidation");
                    }
                    return userEntity.getId();
                } catch (JwtException e) {
                    log.trace("cookie token is invalid");
                    LoginCookieFilter.removeAuthCookie(response);
                } catch (NotFoundException e) {
                    log.trace("userid is unknown");
                    LoginCookieFilter.removeAuthCookie(response);
                }
            }
        }
        return null;
    }

    @CrossOrigin(allowedHeaders = "*", origins = "*")
    @RequestMapping(method = RequestMethod.POST, path = "/oauth/token")
    @ResponseBody
    public TokenResponse requestToken(@Validated TokenRequest tokenRequest) {
        if ("authorization_code".equalsIgnoreCase(tokenRequest.getGrant_type())) {
            Optional<AuthorizationCode> optional = authorizationCodePersistenceService.findByCode(tokenRequest.getCode());
            if (!optional.isPresent() || !optional.get().isValid()) {
                throw new OAuthException(OAuthException.ErrorType.INVALID_REQUEST, "code unknown");
            }
            AuthorizationCode code = optional.get();
            if (!Nulls.notNull(code.getAuthRequest().getRedirect_uri()).equals(Nulls.notNull(tokenRequest.getRedirect_uri())) ) {
                throw new OAuthException(OAuthException.ErrorType.INVALID_REQUEST, "wrong redirect uri");
            }

            // lookup user with rights
            AppUserToken token = appUserTokenService.findById(code.getUserId()).orElseThrow(NotFoundException::new);
            activeUserStore.addUser(token);
            authorizationCodePersistenceService.delete(code.getCode());
            TokenResponse response = buildTokenResponse(code.getAuthRequest().getScope(), token, Nulls.notNull(code.getAuthRequest().getScope()).contains("offline_access"));
            log.info("accessToken expires: {}", jwtTokenService.parseToken(response.getAccessToken()).getExpiration());
            return response;
        } else if ("password".equalsIgnoreCase(tokenRequest.getGrant_type())) {
            if (!StringUtils.hasText(tokenRequest.getUsername()) || StringUtils.hasText(tokenRequest.getPassword())) {
                throw new OAuthException(OAuthException.ErrorType.INVALID_REQUEST, "username and password are required");
            }
            LoginResponse loginResponse = loginService.performLogin(tokenRequest.getUsername(), tokenRequest.getPassword());
            activeUserStore.addUser(loginResponse.getUser());
            AppUserToken token = appUserTokenService.findById(loginResponse.getUser().getId()).orElseThrow(NotFoundException::new);
            return buildTokenResponse(tokenRequest.getScope(), token, true);
        } else if ("refresh_token".equalsIgnoreCase(tokenRequest.getGrant_type())) {
            // validate jwt
            TokenParseResult parsedToken = jwtTokenService.parseToken(tokenRequest.getRefresh_token());
            // lookup user with rights
            AppUserToken token = appUserTokenService.findByUsername(parsedToken.getUser().getUsername()).orElseThrow(NotFoundException::new);
            activeUserStore.addUser(token);
            TokenResponse tokenResponse = buildTokenResponse(tokenRequest.getScope(), token, false);
            tokenResponse.setRefreshToken(tokenRequest.getRefresh_token());
            tokenResponse.setRefreshExpiresIn(getExpiresIn(tokenRequest.getRefresh_token()));
            return tokenResponse;
        }
        throw new OAuthException(OAuthException.ErrorType.INVALID_REQUEST);
    }

    @CrossOrigin(allowedHeaders = "*", origins = "*")
    @RequestMapping(method = RequestMethod.GET, path = "/oauth/.well-known/openid-configuration")
    @ResponseBody
    public WellKnownConfiguration wellKnownOpenidConfiguration(@RequestParam(value = "client_id", required = false) Optional<Long> clientId, HttpServletRequest request) {
        String baseUrl = UrlParts.getBaseUrl(request);

        Set<String> scopesSupported = null;
        if (clientId.isPresent()) {
            AppClientEntity clientEntity = appClientService.findById(clientId.get()).orElseThrow(NotFoundException::new);
            scopesSupported = appCapabilityService.resolve(clientEntity.getCapabilityIds());
        }
        return WellKnownConfiguration.builder()
                .issuer(baseUrl)
                .authorizationEndpoint(UrlParts.concatPaths(baseUrl, "oauth/auth"))
                .tokenEndpoint(UrlParts.concatPaths(baseUrl, "oauth/token"))
                .claimsSupported(Set.of("iss",
                        "sub",
                        "iat",
                        "exp",
                        "scopes",
                        "user"))
                .grantTypesSupported(Set.of("authorization_code", "password", "refresh_token"))
                .responseTypesSupported(Set.of("query"))
                .userinfoEndpoint(UrlParts.concatPaths(baseUrl, "auth/me"))
                .scopesSupported(scopesSupported)
                .build();
    }

    protected TokenResponse buildTokenResponse(String scope, AppUserToken token, boolean withRefresh) {
        TokenResponse.TokenResponseBuilder builder = TokenResponse.builder()
                .tokenType("Bearer")
                .scope(scope);
        if (withRefresh) {
            JwtTokenBundle bundle = jwtTokenService.generateTokenBundle(token);
            return builder
                    .accessToken(bundle.getToken())
                    .expiresIn(getExpiresIn(bundle.getToken()))
                    .refreshToken(bundle.getRefreshToken())
                    .refreshExpiresIn(getExpiresIn(bundle.getRefreshToken()))
                    .build();
        } else {
            String accessToken = jwtTokenService.generateAccessToken(token);
            return builder
                    .accessToken(accessToken)
                    .expiresIn(getExpiresIn(accessToken))
                    .build();
        }
    }


    protected long getExpiresIn(String token) {
        return JwtTokenDecoder.decodeTokenBody(token).getExpiration().getEpochSecond() - Instant.now().getEpochSecond();
    }

}
