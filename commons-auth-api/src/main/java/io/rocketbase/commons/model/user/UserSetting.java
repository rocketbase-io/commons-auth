package io.rocketbase.commons.model.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.annotation.Nullable;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Locale;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(as = SimpleUserSetting.class)
public interface UserSetting extends Serializable {

    @Nullable
    String getLocale();

    /**
     * Java {@link Locale} in format (language-region-variant)<br>
     * valid examples:  zh-Hant-TW, DE, en_US
     */
    void setLocale(@Pattern(regexp = "^[A-Za-z]{2,4}([_-][0-9A-Za-z]{2,8})?([_-][0-9A-Za-z]{2,8})?$")
                   @Size(max = 22)
                           String locale);

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

    @Nullable
    String getCurrentTimeZone();

    /**
     * please use valid formats for {@link java.time.ZoneId} preferred in this format:<br>
     * <ul>
     *     <li>Europe/Berlin</li>
     *     <li>America/Indiana/Petersburg</li>
     *     <li>America/New_York</li>
     * </ul>
     */
    void setCurrentTimeZone(@Pattern(regexp = "^[A-Za-z][A-Za-z0-9~/._+-]+$")
                            @Size(max = 40)
                                    String currentTimeZone);

    @Nullable
    String getDateFormat();

    /**
     * format: yyyy-MM-dd<br>
     * default java dateFormat
     */
    void setDateFormat(@Size(max = 15) String dateFormat);

    @Nullable
    String getTimeFormat();

    /**
     * HH:mm:ss
     * default java dateFormat
     */
    void setTimeFormat(@Size(max = 10) String timeFormat);

    @Nullable
    String getDateTimeFormat();

    /**
     * yyyy-MM-dd HH:mm
     * default java dateFormat
     */
    void setDateTimeFormat(@Size(max = 25) String dateTimeFormat);

}
