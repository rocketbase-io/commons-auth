package io.rocketbase.commons.security;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.jackson.io.JacksonDeserializer;
import io.jsonwebtoken.jackson.io.JacksonSerializer;
import io.jsonwebtoken.lang.Maps;
import io.rocketbase.commons.config.JwtProperties;
import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.model.AppUserToken;
import io.rocketbase.commons.model.SimpleAppUserToken;
import io.rocketbase.commons.model.TokenParseResult;
import io.rocketbase.commons.util.CapacityAuthoritiesConverter;
import io.rocketbase.commons.util.Nulls;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.spec.SecretKeySpec;
import java.io.Serializable;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

import static io.rocketbase.commons.converter.KeyValueConverter.filterInvisibleAndJwtIgnoredKeys;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenService implements Serializable {

    public static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

    public static final String REFRESH_TOKEN = "REFRESH_TOKEN";
    public static final String SCOPES_KEY = "scopes";
    public static final String USER_KEY = "user";

    final JwtProperties jwtProperties;
    final CustomAuthoritiesProvider customAuthoritiesProvider;

    private ObjectMapper objectMapper;

    private ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        }
        return objectMapper;
    }

    private Key getKey() {
        return new SecretKeySpec(Decoders.BASE64.decode(jwtProperties.getSecret()), SIGNATURE_ALGORITHM.getJcaName());
    }

    private Date convert(Instant ldt) {
        return Date.from(ldt.atZone(ZoneOffset.UTC)
                .toInstant());
    }

    private JwtBuilder prepareBuilder(Instant ldt, Duration expiration, String username) {
        return Jwts.builder()
                .serializeToJsonWith(new JacksonSerializer<>(getObjectMapper()))
                .setIssuedAt(convert(ldt))
                .setExpiration(convert(ldt.plus(expiration)))
                .signWith(getKey(), SIGNATURE_ALGORITHM)
                .setSubject(username);
    }

    public TokenParseResult parseToken(String token) throws JwtException {
        if (token == null) {
            throw new JwtException("token is null");
        }
        Jws<Claims> jws = Jwts.parserBuilder()
                .deserializeJsonWith(new JacksonDeserializer(Maps.of(USER_KEY, SimpleAppUserToken.class).build()))
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token);

        SimpleAppUserToken appUserToken = Nulls.notNull(jws.getBody().get(USER_KEY, SimpleAppUserToken.class), SimpleAppUserToken.builderToken()
                .username(jws.getBody().getSubject())
                .build());
        Collection<String> scopes = (Collection) jws.getBody().getOrDefault(SCOPES_KEY, Collections.emptySet());
        appUserToken.setCapabilities(new HashSet<>(scopes != null ? scopes : Collections.emptySet()));
        Instant issuedAt = jws.getBody().getIssuedAt() != null ? Instant.ofEpochMilli(jws.getBody().getIssuedAt().getTime()) : null;
        Instant expiration = jws.getBody().getExpiration() != null ? Instant.ofEpochMilli(jws.getBody().getExpiration().getTime()) : null;
        return new TokenParseResult(token, appUserToken, issuedAt, expiration);
    }

    public JwtTokenBundle generateTokenBundle(AppUserToken appUserToken) {
        Instant now = Instant.now();
        return new JwtTokenBundle(generateAccessToken(now, appUserToken),
                prepareBuilder(now, jwtProperties.getRefreshTokenExpiration(), appUserToken.getUsername())
                        .claim(SCOPES_KEY, Arrays.asList(REFRESH_TOKEN))
                        .compact());
    }

    public String generateAccessToken(AppUserToken appUserToken) {
        return generateAccessToken(Instant.now(), appUserToken);
    }

    protected String generateAccessToken(Instant ldt, AppUserToken appUserToken) {
        Set<GrantedAuthority> scopes = new HashSet<>();
        if (appUserToken.getCapabilities() != null) {
            scopes.addAll(appUserToken.getCapabilities()
                    .stream().map(v -> new SimpleGrantedAuthority(v)).collect(Collectors.toSet()));
        }
        scopes.addAll(customAuthoritiesProvider.getExtraTokenAuthorities(appUserToken));

        SimpleAppUserToken jwtJsonUser = new SimpleAppUserToken(appUserToken);
        jwtJsonUser.setCapabilities(null);
        jwtJsonUser.setKeyValues(filterInvisibleAndJwtIgnoredKeys(jwtJsonUser.getKeyValues()));

        JwtBuilder jwtBuilder = prepareBuilder(ldt, jwtProperties.getAccessTokenExpiration(), appUserToken.getUsername())
                .claim(SCOPES_KEY, CapacityAuthoritiesConverter.convertToDtos(scopes))
                .claim(USER_KEY, jwtJsonUser);

        return jwtBuilder.compact();
    }

    /**
     * check if token string is valid and fit expected username
     *
     * @param token                 jwt string
     * @param username              expected username
     * @param lastTokenInvalidation optional (used to verify if token was created after last invalidation [for example password change or other security issues])
     * @return true in case of valid
     */
    public boolean validateToken(String token, String username, Instant lastTokenInvalidation) {
        TokenParseResult meta;
        try {
            meta = parseToken(token);
        } catch (JwtException e) {
            // should show catch expiration etc. exceptions
            if (log.isTraceEnabled()) {
                log.trace("token is invalid. {}", e.getMessage());
            }
            return false;
        }
        return validateToken(meta, username, lastTokenInvalidation);
    }

    public boolean validateToken(TokenParseResult meta, String username, Instant lastTokenInvalidation) {
        if (!Nulls.noneNullValue(meta.getUser(), meta.getExpiration(), meta.getIssuedAt())) {
            return false;
        }

        if (!meta.getUser().getUsername().equals(username)) {
            // username not fitting
            if (log.isTraceEnabled()) {
                log.trace("token username differs");
            }
            return false;
        }
        if (lastTokenInvalidation == null) {
            return true;
        } else {
            // check if token creation is newer then last token invalidation
            boolean validIssued = lastTokenInvalidation.isBefore(meta.getIssuedAt());
            if (log.isTraceEnabled() && !validIssued) {
                log.trace("token is issued {} before lastTokenInvalidation {}", meta.getIssuedAt(), lastTokenInvalidation);
            }
            return validIssued;
        }
    }

    public boolean validateToken(String token, AppUserEntity user) {
        return validateToken(token, user.getUsername(), user.getLastTokenInvalidation());
    }
}
