package io.rocketbase.commons.model.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.rocketbase.commons.dto.openid.ConnectedAuthorization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import java.util.Set;

@Convert
@Slf4j
public class SetConnectedAuthorizationConverter implements AttributeConverter<Set<ConnectedAuthorization>, String> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Set<ConnectedAuthorization> value) {
        String result = null;
        if (value != null) {
            try {
                result = OBJECT_MAPPER.writeValueAsString(value);
            } catch (JsonProcessingException e) {
                log.error("couldn't convert ConnectedAuthorization to String: {}", e.getMessage());
            }
        }
        return result;
    }

    @Override
    public Set<ConnectedAuthorization> convertToEntityAttribute(String dbData) {
        Set<ConnectedAuthorization> result = null;
        if (StringUtils.hasText(dbData)) {
            try {
                result = OBJECT_MAPPER.readValue(dbData, new TypeReference<Set<ConnectedAuthorization>>() {
                });
            } catch (JsonProcessingException e) {
                log.error("couldn't convert String to ConnectedAuthorization: {}", e.getMessage());
            }
        }
        return result;
    }
}
