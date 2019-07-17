package io.rocketbase.commons.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;

public final class JwtTokenDecoder {

    public static JwtTokenBody decodeTokenBody(String token) {
        if (token != null) {
            String[] split = token.split("\\.");
            if (split.length == 3) {
                try {
                    byte[] body = Base64.getUrlDecoder().decode(split[1]);
                    return new ObjectMapper().readValue(body, JwtTokenBody.class);
                } catch (Exception e) {
                }
            }
        }
        return null;
    }

}
