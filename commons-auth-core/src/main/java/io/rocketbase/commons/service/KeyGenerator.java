package io.rocketbase.commons.service;

import java.security.SecureRandom;

public final class KeyGenerator {

    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String SPECIAL_SIGN = "!#$%&()*+,-./:;<=>?@[]^_`{|}~";
    private static final String ALPHABET_WITH_SPECIAL = ALPHABET + SPECIAL_SIGN;
    private static SecureRandom RANDOM = new SecureRandom();

    /**
     * random string with alphabet of 0-9 + a-z (lower/upper)
     *
     * @param length of string
     * @return random string
     */
    public static String random(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++)
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        return sb.toString();
    }

    /**
     * random string with alphabet of 0-9 + a-z (lower/upper)<br>
     * plus special sings of "<b>!#$%&()*+,-./:;<=>?@[]^_`{|}~</b>"
     *
     * @param length of string
     * @return random string
     */
    public static String randomWithSpecialSigns(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++)
            sb.append(ALPHABET_WITH_SPECIAL.charAt(RANDOM.nextInt(ALPHABET_WITH_SPECIAL.length())));
        return sb.toString();
    }
}
