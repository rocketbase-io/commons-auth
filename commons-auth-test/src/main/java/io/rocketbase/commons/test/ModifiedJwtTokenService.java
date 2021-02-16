package io.rocketbase.commons.test;

import io.rocketbase.commons.config.JwtProperties;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.security.EmptyCustomAuthoritiesProvider;
import io.rocketbase.commons.security.JwtTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
public class ModifiedJwtTokenService extends JwtTokenService {

    @Autowired
    public ModifiedJwtTokenService(JwtProperties jwtProperties) {
        super(jwtProperties, new EmptyCustomAuthoritiesProvider());
    }

    public String generateExpiredToken(AppUserEntity user) {
        // TODO: fix
        return generateAccessToken(Instant.now().minusSeconds(60 * 60 * 24 * 100), null); // user
    }
}
