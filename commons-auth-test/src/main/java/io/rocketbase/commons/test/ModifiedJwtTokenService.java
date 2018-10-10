package io.rocketbase.commons.test;

import io.rocketbase.commons.config.JwtProperties;
import io.rocketbase.commons.model.AppUser;
import io.rocketbase.commons.security.EmptyCustomAuthoritiesProvider;
import io.rocketbase.commons.security.JwtTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@Component
public class ModifiedJwtTokenService extends JwtTokenService {

    @Autowired
    public ModifiedJwtTokenService(JwtProperties jwtProperties) {
        super(jwtProperties, new EmptyCustomAuthoritiesProvider());
    }

    public String generateExpiredToken(AppUser user) {
        return generateAccessToken(LocalDateTime.now(ZoneOffset.UTC).minusDays(100), user.getUsername(), user.getAuthorities());
    }
}
