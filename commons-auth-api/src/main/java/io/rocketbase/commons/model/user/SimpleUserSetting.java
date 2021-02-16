package io.rocketbase.commons.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimpleUserSetting implements UserSetting {

    private String locale;

    private String currentTimeZone;

    private String dateFormat;

    private String timeFormat;

    private String dateTimeFormat;

    public SimpleUserSetting(UserSetting other) {
        this.locale = other.getLocale();
        this.currentTimeZone = other.getCurrentTimeZone();
        this.dateFormat = other.getDateFormat();
        this.timeFormat = other.getTimeFormat();
        this.dateTimeFormat = other.getDateTimeFormat();
    }

    public static UserSetting init(Locale locale) {
        return SimpleUserSetting.builder()
                .locale(locale.toLanguageTag())
                .currentTimeZone(null)
                .dateFormat(((SimpleDateFormat) SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, locale)).toPattern())
                .timeFormat(((SimpleDateFormat) SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, locale)).toPattern())
                .dateTimeFormat(((SimpleDateFormat) SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, locale)).toPattern())
                .build();
    }
}
