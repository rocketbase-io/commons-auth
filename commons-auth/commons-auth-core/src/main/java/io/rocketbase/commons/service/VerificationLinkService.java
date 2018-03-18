package io.rocketbase.commons.service;

import io.rocketbase.commons.config.AuthConfiguration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.Serializable;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Slf4j
@Service
public class VerificationLinkService {

    private static final String ALGORITHM = "Blowfish";
    @Resource
    private AuthConfiguration authConfiguration;
    private Key key;

    @SneakyThrows
    @PostConstruct
    void postConstruct() {
        key = new SecretKeySpec(authConfiguration.getKeySecret().getBytes(), ALGORITHM);
    }

    @SneakyThrows
    public String generateKey(String username, ActionType type, long expiresInMinutes) {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encValue = cipher.doFinal(
                new VerificationToken(username, LocalDateTime.now()
                        .plusMinutes(expiresInMinutes), type)
                        .serialize()
                        .getBytes());

        return Base64.getEncoder()
                .encodeToString(encValue);
    }

    public VerificationToken parseKey(String encKey) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] decValue = cipher.doFinal(Base64.getDecoder()
                    .decode(encKey));
            return VerificationToken.parseString(String.valueOf(decValue));
        } catch (Exception e) {
            log.debug("unable to decode key", e.getMessage());
            return new VerificationToken(null, null, null);
        }
    }

    public enum ActionType {

        VERIFICATION("/auth/verify"), PASSWORD_RESET(("/auth/password-reset"));

        @Getter
        private String apiPath;

        ActionType(String apiPath) {
            this.apiPath = apiPath;
        }

    }

    @RequiredArgsConstructor
    @Getter
    public static class VerificationToken implements Serializable {

        private final String username;
        private final LocalDateTime exp;
        private final ActionType type;

        public static VerificationToken parseString(String serialization) {
            try {
                String values[] = serialization.split("\\|");
                LocalDateTime exp = LocalDateTime.parse(values[1], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                ActionType type = ActionType.valueOf(values[2]);
                return new VerificationToken(values[0], exp, type);
            } catch (Exception e) {
                log.warn("couldn't parse token {}, error: {}", serialization, e.getMessage());
                return new VerificationToken(null, null, null);
            }
        }

        public String serialize() {
            return String.format("%s|%s|%s", username, DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(exp), type.ordinal());
        }

        public boolean isValid(ActionType type) {
            if (exp == null || exp.isBefore(LocalDateTime.now())) {
                return false;
            }
            return type.equals(this.type);
        }
    }


}
