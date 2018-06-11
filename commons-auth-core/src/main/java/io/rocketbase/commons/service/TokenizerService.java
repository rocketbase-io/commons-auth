package io.rocketbase.commons.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.Serializable;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

@Slf4j
public class TokenizerService {

    private static final String ALGORITHM = "Blowfish";

    private Key key;

    /**
     * construct with random password
     */
    public TokenizerService() {
        this(KeyGenerator.randomWithSpecialSigns(32));
    }

    /**
     * construct with given password
     */
    public TokenizerService(String keySecret) {
        key = new SecretKeySpec(keySecret.getBytes(), ALGORITHM);
    }

    /**
     * @param username
     * @param keyValues        optional keyValue map
     * @param expiresInMinutes
     * @return
     */
    @SneakyThrows
    public String generateToken(String username, Map<String, String> keyValues, long expiresInMinutes) {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encValue = cipher.doFinal(
                new Token(username, LocalDateTime.now()
                        .plusMinutes(expiresInMinutes), keyValues)
                        .serialize()
                        .getBytes());

        return Base64Utils.encodeToUrlSafeString(encValue);
    }

    /**
     * @param cryptedToken
     * @return allways in instance of token<br>
     * * need to check isValid
     */
    public Token parseToken(String cryptedToken) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] decValue = cipher.doFinal(Base64Utils.decodeFromUrlSafeString(cryptedToken));
            return Token.parseString(new String(decValue));
        } catch (Exception e) {
            log.debug("unable to decode key", e.getMessage());
            return new Token(null, null, null);
        }
    }

    @RequiredArgsConstructor
    @Getter
    public static class Token implements Serializable {

        public static final String DEF_SPLITTER = "||";
        public static final String KV_SPLITTER = "::";
        public static final String LIST_SPLITTER = ";;";

        private final String username;
        private final LocalDateTime exp;
        private final Map<String, String> keyValues;

        /**
         * @param serialization
         * @return
         */
        public static Token parseString(String serialization) {
            try {
                String values[] = serialization.split("\\|\\|");
                LocalDateTime exp = LocalDateTime.parse(values[1], DateTimeFormatter.ISO_LOCAL_DATE_TIME);

                Map<String, String> keyValues = null;
                if (values.length == 4) {
                    String mapStr = values[3];
                    String[] kVs = mapStr.split("\\;\\;");
                    keyValues = new HashMap<>();
                    for (String kv : kVs) {
                        String[] kvSplit = kv.split("\\:\\:");
                        keyValues.put(kvSplit[0], kvSplit[1]);
                    }
                }
                return new Token(values[0], exp, keyValues);
            } catch (Exception e) {
                log.warn("couldn't parse token {}, error: {}", serialization, e.getMessage());
                return new Token(null, null, null);
            }
        }

        public String serialize() {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(username).append(DEF_SPLITTER);
            stringBuffer.append(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(exp)).append(DEF_SPLITTER);
            stringBuffer.append(new Random().nextInt(500));
            if (keyValues != null) {
                stringBuffer.append(DEF_SPLITTER);
                Iterator<Map.Entry<String, String>> iterator = keyValues.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, String> entry = iterator.next();
                    String key = entry.getKey(), value = entry.getValue();

                    Assert.hasLength(key, "key couldn't be empty");
                    Assert.state(!(key.contains(LIST_SPLITTER) || key.contains(KV_SPLITTER) || key.contains(DEF_SPLITTER)), "key couldn't contain '||', '::' or ';;'");

                    Assert.hasLength(value, "value couldn't be empty");
                    Assert.state(!(value.contains(LIST_SPLITTER) || value.contains(KV_SPLITTER) || value.contains(DEF_SPLITTER)), "value couldn't contain '||', '::' or ';;'");

                    stringBuffer.append(key).append(KV_SPLITTER).append(value);
                    if (iterator.hasNext()) {
                        stringBuffer.append(LIST_SPLITTER);
                    }
                }
            }
            return stringBuffer.toString();
        }

        public boolean isValid() {
            if (exp == null || exp.isBefore(LocalDateTime.now())) {
                return false;
            }
            return true;
        }
    }
}
