package io.rocketbase.commons.test;

import io.rocketbase.commons.config.JwtProperties;
import io.rocketbase.commons.converter.AppUserConverter;
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

    private final AppUserConverter appUserConverter;

    @Autowired
    public ModifiedJwtTokenService(JwtProperties jwtProperties, AppUserConverter appUserConverter) {
        super(jwtProperties, new EmptyCustomAuthoritiesProvider());
        this.appUserConverter = appUserConverter;
    }

    public String generateExpiredToken(AppUserEntity user) {
        return generateAccessToken(Instant.now().minusSeconds(60 * 60 * 24 * 100), appUserConverter.toToken(user));
    }
}
