package io.rocketbase.commons.model.embedded;

import io.rocketbase.commons.model.user.UserSetting;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Data
@NoArgsConstructor
public class UserSettingJpaEmbedded implements UserSetting {

    @Column(length = 22)
    private String locale;

    @Column(name="current_time_zone", length = 40)
    private String currentTimeZone;

    @Column(name = "date_format",length = 15)
    private String dateFormat;

    @Column(name = "time_format",length = 10)
    private String timeFormat;

    @Column(name = "date_time_format",length = 25)
    private String dateTimeFormat;

    public UserSettingJpaEmbedded(UserSetting userSetting) {
        if (userSetting != null) {
            locale = userSetting.getLocale();
            currentTimeZone = userSetting.getCurrentTimeZone();
            dateFormat = userSetting.getDateFormat();
            timeFormat = userSetting.getTimeFormat();
            dateTimeFormat = userSetting.getDateTimeFormat();
        }
    }
}
