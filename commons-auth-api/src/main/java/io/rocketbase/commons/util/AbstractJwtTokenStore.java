package io.rocketbase.commons.util;

import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;

import java.time.Instant;

public abstract class AbstractJwtTokenStore implements JwtTokenStore {

    protected final JwtTokenBundle tokenBundle;

    protected String header = HttpHeaders.AUTHORIZATION;
    protected String tokenPrefix = "Bearer ";

    protected String lastToken;
    protected Instant exp;


    public AbstractJwtTokenStore(JwtTokenBundle tokenBundle) {
        this.tokenBundle = tokenBundle;
        Assert.notNull(tokenBundle, "tokenBundle is required");
    }

    /**
     * will check token's expiration date
     *
     * @return true if token is valid for at least 60 sec
     */
    @Override
    public boolean checkTokenNeedsRefresh() {
        return checkTokenNeedsRefresh(60);
    }

    /**
     * will check token's expiration date
     *
     * @param seconds how long token should be valid
     * @return true if token is valid for at least 60 sec
     */
    @Override
    public boolean checkTokenNeedsRefresh(long seconds) {
        if (tokenBundle.getRefreshToken() != null) {
            if (lastToken == null || !tokenBundle.getToken().equals(lastToken)) {
                JwtTokenBody tokenBody = JwtTokenDecoder.decodeTokenBody(tokenBundle.getToken());
                if (tokenBody == null || tokenBody.getExpiration() == null) {
                    // in case of broken tokens
                    return true;
                }
                exp = tokenBody.getExpiration();
                lastToken = tokenBundle.getToken();
            }

            return Instant.now()
                    .plusSeconds(seconds)
                    .isAfter(exp);
        }
        return false;
    }

    protected String getTokenHeader(String token) {
        return String.format("%s%s", tokenPrefix, token);
    }

    @Override
    public JwtTokenBundle getTokenBundle() {
        return this.tokenBundle;
    }

    @Override
    public String getHeaderName() {
        return header;
    }

    @Override
    public String getTokenHeader() {
        return getTokenHeader(tokenBundle.getToken());
    }
}
