package io.rocketbase.commons.model.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Locale;

@JsonDeserialize(as = SimpleUserSetting.class)
public interface UserSetting extends Serializable {

    String getLocale();

    /**
     * Java {@link Locale} in format (language-region-variant)<br>
     * valid examples:  zh-Hant-TW, DE, en_US
     */
    @Nullable
    @Pattern(regexp = "^[A-Za-z]{2,4}([_-][0-9A-Za-z]{2,8})?([_-][0-9A-Za-z]{2,8})?$")
    @Size(max = 22)
    void setLocale(String locale);

    /**
     * converts locale value or use LocaleContextHolder when not set
     *
     * @return always a locale
     */
    default Locale getCurrentLocale() {
        if (getLocale() != null) {
            return Locale.forLanguageTag(getLocale());
        }
        return LocaleContextHolder.getLocale();
    }

    String getCurrentTimeZone();

    /**
     * please use valid formats for {@link java.time.ZoneId} preferred in this format:<br>
     * <ul>
     *     <li>Europe/Berlin</li>
     *     <li>America/Indiana/Petersburg</li>
     *     <li>America/New_York</li>
     * </ul>
     */
    @Nullable
    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9~/._+-]+$")
    @Size(max = 40)
    void setCurrentTimeZone(String currentTimeZone);

    String getDateFormat();

    /**
     * format: yyyy-MM-dd<br>
     * default java dateFormat
     */
    @Nullable
    @Size(max = 15)
    void setDateFormat(String dateFormat);

    String getTimeFormat();

    /**
     * HH:mm:ss
     * default java dateFormat
     */
    @Nullable
    @Size(max = 10)
    void setTimeFormat(String timeFormat);

    String getDateTimeFormat();

    /**
     * yyyy-MM-dd HH:mm
     * default java dateFormat
     */
    @Nullable
    @Size(max = 25)
    void setDateTimeFormat(String dateTimeFormat);

}
