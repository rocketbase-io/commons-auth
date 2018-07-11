package io.rocketbase.commons.security;

import io.jsonwebtoken.*;
import io.rocketbase.commons.config.JwtProperties;
import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.model.AppUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.annotation.Resource;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenService implements Serializable {

    public static final String REFRESH_TOKEN = "REFRESH_TOKEN";

    final JwtProperties jwtProperties;

    @Resource
    CustomAuthoritiesProvider customAuthoritiesProvider;

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Collection<GrantedAuthority> getAuthoritiesFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        List roles = (List) claims.getOrDefault("scopes", Collections.emptyList());
        List<GrantedAuthority> result = new ArrayList<>();
        for (Object r : roles) {
            result.add(new SimpleGrantedAuthority(String.valueOf(r)));
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
                .setSigningKey(jwtProperties.getSecret())
                .parseClaimsJws(token)
                .getBody();
    }

    public JwtTokenBundle generateTokenBundle(AppUser appUser) {
        return generateTokenBundle(appUser.getUsername(), appUser.getAuthorities());
    }

    public JwtTokenBundle generateTokenBundle(String username, Collection<? extends GrantedAuthority> authorities) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        return new JwtTokenBundle(generateAccessToken(now, username, authorities),
                prepareBuilder(now, jwtProperties.getRefreshTokenExpiration(), username)
                        .claim("scopes", Arrays.asList(REFRESH_TOKEN))
                        .compact());
    }

    public String generateAccessToken(String username, Collection<? extends GrantedAuthority> authorities) {
        return generateAccessToken(LocalDateTime.now(ZoneOffset.UTC), username, authorities);
    }

    protected String generateAccessToken(LocalDateTime ldt, String username, Collection<? extends GrantedAuthority> authorities) {
        List<GrantedAuthority> scopes = new ArrayList<>();
        scopes.addAll(authorities);
        scopes.addAll(customAuthoritiesProvider.getExtraTokenAuthorities(username));

        return prepareBuilder(ldt, jwtProperties.getAccessTokenExpiration(), username)
                .claim("scopes", scopes.stream().map(a -> a.getAuthority()).collect(Collectors.toSet()))
                .compact();
    }

    private JwtBuilder prepareBuilder(LocalDateTime ldt, long expirationMinutes, String username) {
        return Jwts.builder()
                .setIssuedAt(convert(ldt))
                .setExpiration(convert(ldt.plusMinutes(expirationMinutes)))
                .signWith(SignatureAlgorithm.HS512, jwtProperties.getSecret())
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
