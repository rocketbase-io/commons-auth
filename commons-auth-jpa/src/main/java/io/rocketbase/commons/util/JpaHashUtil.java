package io.rocketbase.commons.util;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public class JpaHashUtil {

    public static String hashValue(String value) {
        if (value == null) {
            return null;
        }
        return Hashing.sha256().hashString(value, StandardCharsets.UTF_8).toString();
    }
}
