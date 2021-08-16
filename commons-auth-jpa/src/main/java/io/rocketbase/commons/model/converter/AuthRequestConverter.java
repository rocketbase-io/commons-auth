package io.rocketbase.commons.model.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.rocketbase.commons.dto.authentication.oauth.AuthRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;

@Convert
@Slf4j
public class AuthRequestConverter implements AttributeConverter<AuthRequest, String> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(AuthRequest attribute) {
        String result = null;
        if (attribute != null) {
            try {
                result = OBJECT_MAPPER.writeValueAsString(attribute);
            } catch (JsonProcessingException e) {
                log.error("couldn't convert AuthRequest to String: {}", e.getMessage());
            }
        }
        return result;
    }

    @Override
    public AuthRequest convertToEntityAttribute(String dbData) {
        AuthRequest result = null;
        if (StringUtils.hasText(dbData)) {
            try {
                result = OBJECT_MAPPER.readValue(dbData, AuthRequest.class);
            } catch (JsonProcessingException e) {
                log.error("couldn't convert String to AuthRequest: {}", e.getMessage());
            }
        }
        return result;
    }
}
