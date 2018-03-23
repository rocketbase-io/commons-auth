package io.rocketbase.commons.test;

import io.rocketbase.commons.model.AppUser;
import io.rocketbase.commons.security.JwtTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class ModifiedJwtTokenService extends JwtTokenService {

    public String generateExpiredToken(AppUser user) {
        return generateAccessToken(new Date(new Date().getTime() - 1000 * 60 * 60 * 24), user);
    }
}
