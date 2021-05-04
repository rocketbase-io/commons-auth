package io.rocketbase.commons.model.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.rocketbase.commons.model.user.OnlineProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import java.util.Set;

@Convert
@Slf4j
public class SetOnlineProfileConverter implements AttributeConverter<Set<OnlineProfile>, String> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Set<OnlineProfile> attribute) {
        String result = null;
        if (attribute != null) {
            try {
                result = OBJECT_MAPPER.writeValueAsString(attribute);
            } catch (JsonProcessingException e) {
                log.error("couldn't convert OnlineProfile to String: {}", e.getMessage());
            }
        }
        return result;
    }

    @Override
    public Set<OnlineProfile> convertToEntityAttribute(String dbData) {
        Set<OnlineProfile> result = null;
        if (StringUtils.hasText(dbData)) {
            try {
                result = OBJECT_MAPPER.readValue(dbData, new TypeReference<Set<OnlineProfile>>() {
                });
            } catch (JsonProcessingException e) {
                log.error("couldn't convert String to OnlineProfile: {}", e.getMessage());
            }
        }
        return result;
    }
}
