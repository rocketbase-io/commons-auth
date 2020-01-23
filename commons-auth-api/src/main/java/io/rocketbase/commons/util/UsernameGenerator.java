package io.rocketbase.commons.util;

import org.springframework.util.StringUtils;

import java.text.Normalizer;

public abstract class UsernameGenerator {

    private static String[][] UMLAUT_REPLACEMENTS = {{"Ä", "Ae"}, {"Ü", "Ue"}, {"Ö", "Oe"}, {"ä", "ae"}, {"ü", "ue"}, {"ö", "oe"}, {"ß", "ss"}};

    public static String replaceUmlaute(String orig) {
        String result = Nulls.notNull(orig) + "";
        for (int i = 0; i < UMLAUT_REPLACEMENTS.length; i++) {
            result = result.replace(UMLAUT_REPLACEMENTS[i][0], UMLAUT_REPLACEMENTS[i][1]);
        }
        return result;
    }

    public static String normalizeString(String input) {
        String result = Nulls.notNull(input) + "";
        result = replaceUmlaute(result);
        result = Normalizer.normalize(result, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
        return result.toLowerCase().replaceAll("[^a-z0-9\\.\\-\\_]*", "");
    }

    public static String byFirstAndLastName(String firstName, String lastName) {
        String result = "";
        if (!StringUtils.isEmpty(firstName)) {
            result += firstName;
        }
        if (!StringUtils.isEmpty(lastName)) {
            if (result.length() > 0) {
                result += ".";
            }
            result += lastName;
        }
        return normalizeString(result);
    }

    public static String byEmail(String email) {
        int atIndex = Nulls.notNull(email).lastIndexOf("@");
        if (atIndex <= 0) {
            return null;
        }
        return normalizeString(email.substring(0, atIndex));
    }
}
