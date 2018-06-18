package io.rocketbase.commons.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.util.Locale;

import static java.time.temporal.ChronoField.*;

@Slf4j
public class SimpleTokenService {

    private static final DateTimeFormatter DTF = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendValue(YEAR, 4, 4, SignStyle.EXCEEDS_PAD)
            .appendValue(DAY_OF_YEAR, 3)
            .appendValue(HOUR_OF_DAY, 2)
            .appendValue(MINUTE_OF_HOUR, 2)
            .appendValue(SECOND_OF_MINUTE, 2)
            .toFormatter(Locale.ENGLISH);

    public static String generateToken(String username, long expiresInMinutes) {
        return KeyGenerator.random(4) + LocalDateTime.now().plusMinutes(expiresInMinutes).format(DTF) + KeyGenerator.random(4) + ":" + username;
    }

    public static Token parseToken(String token) {
        return Token.parseString(token);
    }

    @RequiredArgsConstructor
    @Getter
    public static class Token implements Serializable {

        public static final String DEF_SPLITTER = ":";

        private final String username;
        private final LocalDateTime exp;

        /**
         * @param serialization
         * @return
         */
        public static Token parseString(String serialization) {
            try {
                String values[] = serialization.split("\\:");

                LocalDateTime expired = null;
                if (values[0] != null && values[0].length() == 21) {
                    try {
                        expired = LocalDateTime.parse(values[0].substring(4, 17), DTF);
                    } catch (Exception e) {
                    }
                }
                return new Token(values[1], expired);
            } catch (Exception e) {
                return new Token(null, null);
            }
        }

        public boolean isValid() {
            if (exp == null || exp.isBefore(LocalDateTime.now())) {
                return false;
            }
            return true;
        }
    }
}
