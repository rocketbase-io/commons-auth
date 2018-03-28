package io.rocketbase.commons.security;

import io.jsonwebtoken.*;
import io.rocketbase.commons.config.JwtConfiguration;
import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.model.AppUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Function;

@Slf4j
@Component
public class JwtTokenService implements Serializable {

    public static final String REFRESH_TOKEN = "REFRESH_TOKEN";

    @Resource
    JwtConfiguration jwtConfiguration;

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
        return LocalDateTime.ofInstant(issuedAt.toInstant(), ZoneOffset.UTC);
    }

    public LocalDateTime getExpirationDateFromToken(String token) {
        Date expiration = getClaimFromToken(token, Claims::getExpiration);
        return LocalDateTime.ofInstant(expiration.toInstant(), ZoneOffset.UTC);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtConfiguration.getSecret())
                .parseClaimsJws(token)
                .getBody();
    }

    public JwtTokenBundle generateTokenBundle(AppUser user) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        return new JwtTokenBundle(generateAccessToken(now, user),
                prepareBuilder(now, jwtConfiguration.getRefreshTokenExpiration(), user.getUsername())
                        .claim("scopes", Arrays.asList(REFRESH_TOKEN))
                        .compact());
    }


    public String generateAccessToken(AppUser user) {
        return generateAccessToken(LocalDateTime.now(ZoneOffset.UTC), user);
    }

    protected String generateAccessToken(LocalDateTime ldt, AppUser user) {
        return prepareBuilder(ldt, jwtConfiguration.getAccessTokenExpiration(), user.getUsername())
                .claim("scopes", user.getRoles())
                .compact();
    }

    private JwtBuilder prepareBuilder(LocalDateTime ldt, long expirationMinutes, String username) {
        return Jwts.builder()
                .setIssuedAt(convert(ldt))
                .setExpiration(convert(ldt.plusMinutes(expirationMinutes)))
                .signWith(SignatureAlgorithm.HS512, jwtConfiguration.getSecret())
                .setSubject(username);
    }

    private Date convert(LocalDateTime ldt) {
        return Date.from(ldt.atZone(ZoneOffset.UTC)
                .toInstant());
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
