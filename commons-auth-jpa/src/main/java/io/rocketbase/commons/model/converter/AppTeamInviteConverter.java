package io.rocketbase.commons.model.converter;

import io.rocketbase.commons.dto.appteam.AppTeamInvite;
import io.rocketbase.commons.dto.appteam.AppTeamRole;
import org.springframework.util.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;

@Convert
public class AppTeamInviteConverter implements AttributeConverter<AppTeamInvite, String> {

    @Override
    public String convertToDatabaseColumn(AppTeamInvite attribute) {
        String result = null;
        if (attribute != null) {
            result = String.format("%d;%s", attribute.getTeamId(), attribute.getRole().name());
        }
        return result;
    }

    @Override
    public AppTeamInvite convertToEntityAttribute(String dbData) {
        if (!StringUtils.isEmpty(dbData) && dbData.matches("^[0-9]+;[a-zA-Z]+$")) {
            String[] split = dbData.split(";");
            return new AppTeamInvite(Long.parseLong(split[0]), AppTeamRole.valueOf(split[1]));
        }
        return null;
    }
}
