package io.rocketbase.commons.security;

import io.jsonwebtoken.*;
import io.rocketbase.commons.config.JwtProperties;
import io.rocketbase.commons.converter.AppUserConverter;
import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.model.AppUserToken;
import io.rocketbase.commons.model.SimpleAppUserToken;
import io.rocketbase.commons.util.RolesAuthoritiesConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenService implements Serializable {

    public static final String REFRESH_TOKEN = "REFRESH_TOKEN";
    public static final String ROLES_KEY = "scopes";
    public static final String USER_ID_KEY = "user_id";
    public static final String FIRST_NAME_KEY = "given_name";
    public static final String LAST_NAME_KEY = "family_name";
    public static final String EMAIL_KEY = "email";
    public static final String AVATAR_KEY = "picture";
    public static final String KEY_VALUE_PREFIX = "kv_";

    final JwtProperties jwtProperties;
    final CustomAuthoritiesProvider customAuthoritiesProvider;

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Collection<GrantedAuthority> getAuthoritiesFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        List roles = (List) claims.getOrDefault(ROLES_KEY, Collections.emptyList());
        List<GrantedAuthority> result = new ArrayList<>();
        for (Object r : roles) {
            result.add(new SimpleGrantedAuthority(String.valueOf(r)));
        }
        return result;
    }

    public Instant getIssuedAtDateFromToken(String token) {
        Date issuedAt = getClaimFromToken(token, Claims::getIssuedAt);
        return Instant.ofEpochMilli(issuedAt.getTime());
    }

    public Instant getExpirationDateFromToken(String token) {
        Date expiration = getClaimFromToken(token, Claims::getExpiration);
        return Instant.ofEpochMilli(expiration.getTime());
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecret())
                .parseClaimsJws(token)
                .getBody();
    }

    public AppUserToken parseToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        Map<String, String> keyValues = null;
        for (String key : claims.keySet()) {
            if (key.startsWith(KEY_VALUE_PREFIX)) {
                if (keyValues == null) {
                    keyValues = new HashMap<>();
                }
                keyValues.put(key.replaceAll("^" + KEY_VALUE_PREFIX, ""), claims.get(key, String.class));
            }
        }

        return SimpleAppUserToken.builder()
                .id(claims.get(USER_ID_KEY, String.class))
                .username(claims.getSubject())
                .firstName(claims.get(FIRST_NAME_KEY, String.class))
                .lastName(claims.get(LAST_NAME_KEY, String.class))
                .email(claims.get(EMAIL_KEY, String.class))
                .avatar(claims.get(AVATAR_KEY, String.class))
                .roles((List) claims.getOrDefault(ROLES_KEY, Collections.emptyList()))
                .keyValueMap(keyValues)
                .build();
    }


    public JwtTokenBundle generateTokenBundle(AppUserToken appUserToken) {
        Instant now = Instant.now();
        return new JwtTokenBundle(generateAccessToken(now, appUserToken),
                prepareBuilder(now, jwtProperties.getRefreshTokenExpiration(), appUserToken.getUsername())
                        .claim(USER_ID_KEY, appUserToken.getId())
                        .claim(ROLES_KEY, Arrays.asList(REFRESH_TOKEN))
                        .compact());
    }

    public String generateAccessToken(AppUserToken appUserToken) {
        return generateAccessToken(Instant.now(), appUserToken);
    }

    protected String generateAccessToken(Instant ldt, AppUserToken appUserToken) {
        List<GrantedAuthority> scopes = new ArrayList<>();
        scopes.addAll(RolesAuthoritiesConverter.convert(appUserToken.getRoles()));
        scopes.addAll(customAuthoritiesProvider.getExtraTokenAuthorities(appUserToken));

        JwtBuilder jwtBuilder = prepareBuilder(ldt, jwtProperties.getAccessTokenExpiration(), appUserToken.getUsername())
                .claim(ROLES_KEY, RolesAuthoritiesConverter.convertToDtos(scopes))
                .claim(USER_ID_KEY, appUserToken.getId())
                .claim(FIRST_NAME_KEY, appUserToken.getFirstName())
                .claim(LAST_NAME_KEY, appUserToken.getLastName())
                .claim(EMAIL_KEY, appUserToken.getEmail())
                .claim(AVATAR_KEY, appUserToken.getAvatar());

        Map<String, String> keyValues = AppUserConverter.filterInvisibleKeys(appUserToken.getKeyValues());
        if (keyValues != null) {
            for (Map.Entry<String, String> entry : keyValues.entrySet()) {
                jwtBuilder.claim(KEY_VALUE_PREFIX + entry.getKey(), entry.getValue());
            }
        }
        return jwtBuilder.compact();
    }

    private JwtBuilder prepareBuilder(Instant ldt, long expirationMinutes, String username) {
        return Jwts.builder()
                .setIssuedAt(convert(ldt))
                .setExpiration(convert(ldt.plusSeconds(expirationMinutes*60)))
                .signWith(SignatureAlgorithm.HS512, jwtProperties.getSecret())
                .setSubject(username);
    }

    private Date convert(Instant ldt) {
        return Date.from(ldt.atZone(ZoneOffset.UTC)
                .toInstant());
    }

    /**
     * check if token string is valid and fit expected username
     *
     * @param token
     * @param username
     * @param lastTokenInvalidation
     * @return true in case of valid
     */
    public Boolean validateToken(String token, String username, Instant lastTokenInvalidation) {
        try {
            getAllClaimsFromToken(token);
        } catch (JwtException e) {
            // should show catch expiration etc. exceptions
            if (log.isTraceEnabled()) {
                log.trace("token is invalid", e);
            }
            return false;
        }
        if (!getUsernameFromToken(token).equals(username)) {
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
            boolean validIssued = lastTokenInvalidation.isBefore(getIssuedAtDateFromToken(token));
            if (log.isTraceEnabled() && !validIssued) {
                log.trace("token is issued {} before lastTokenInvalidation {}", getIssuedAtDateFromToken(token), lastTokenInvalidation);
            }
            return validIssued;
        }
    }

    public Boolean validateToken(String token, AppUserEntity user) {
        return validateToken(token, user.getUsername(), user.getLastTokenInvalidation());
    }
}
