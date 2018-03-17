package io.rocketbase.commons.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultClock;
import io.rocketbase.commons.config.AuthConfiguration;
import io.rocketbase.commons.converter.AppUserConverter;
import io.rocketbase.commons.dto.JwtTokenBundle;
import io.rocketbase.commons.model.AppUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;

@Slf4j
@Component
public class JwtTokenService implements Serializable {

    public static final String REFRESH_TOKEN = "REFRESH_TOKEN";

    @Resource
    private AuthConfiguration authConfiguration;

    @Resource
    private AppUserConverter appUserConverter;

    private AuthConfiguration.JwtConfiguration getJwt() {
        return authConfiguration.getJwt();
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Collection<? extends GrantedAuthority> getAuthoritiesFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        List roles = (List) claims.getOrDefault("scopes", Collections.emptyList());
        List<GrantedAuthority> result = new ArrayList<>();
        for (Object r : roles) {
            result.add(new SimpleGrantedAuthority(String.format("ROLE_%s", String.valueOf(r))));
        }
        return result;
    }

    public LocalDateTime getIssuedAtDateFromToken(String token) {
        Date issuedAt = getClaimFromToken(token, Claims::getIssuedAt);
        return LocalDateTime.ofInstant(issuedAt.toInstant(), ZoneId.systemDefault());
    }

    public LocalDateTime getExpirationDateFromToken(String token) {
        Date expiration = getClaimFromToken(token, Claims::getExpiration);
        return LocalDateTime.ofInstant(expiration.toInstant(), ZoneId.systemDefault());
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(getJwt().getSecret())
                .parseClaimsJws(token)
                .getBody();
    }

    public JwtTokenBundle generateTokenBundle(AppUser user) {
        Date now = DefaultClock.INSTANCE.now();

        return new JwtTokenBundle(generateAccessToken(now, user),
                prepareBuilder(now, getJwt().getExpiration().getRefreshToken(), user.getUsername())
                        .claim("scopes", Arrays.asList(REFRESH_TOKEN))
                        .compact());
    }


    public String generateAccessToken(AppUser user) {
        return generateAccessToken(DefaultClock.INSTANCE.now(), user);
    }

    private String generateAccessToken(Date now, AppUser user) {
        return prepareBuilder(now, getJwt().getExpiration().getAccessToken(), user.getUsername())
                .claim("scopes", user.getRoles())
                .claim("user", appUserConverter.fromEntity(user))
                .compact();
    }

    private JwtBuilder prepareBuilder(final Date createdDate, long expiration, String username) {
        return Jwts.builder()
                .setIssuedAt(createdDate)
                .setExpiration(new Date(createdDate.getTime() + expiration))
                .signWith(SignatureAlgorithm.HS512, getJwt().getSecret())
                .setSubject(username);
    }

    public Boolean validateToken(String token, AppUser user) {
        try {
            getAllClaimsFromToken(token);
        } catch (JwtException e) {
            // should show catch expiration etc. exceptions
            if (log.isTraceEnabled()) {
                log.trace("token is invalid", e);
            }
            return false;
        }
        if (!getUsernameFromToken(token).equals(user.getUsername())) {
            // username not fitting
            if (log.isTraceEnabled()) {
                log.trace("token username differs");
            }
            return false;
        }
        if (user.getLastTokenInvalidation() == null) {
            return true;
        } else {
            // check if token creation is newer then last token invalidation
            boolean validIssued = user.getLastTokenInvalidation().isBefore(getIssuedAtDateFromToken(token));
            if (log.isTraceEnabled() && !validIssued) {
                log.trace("token is issued {} before lastTokenInvalidation {}", getIssuedAtDateFromToken(token), user.getLastTokenInvalidation());
            }
            return validIssued;
        }
    }
}
