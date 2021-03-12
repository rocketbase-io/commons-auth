package io.rocketbase.commons.model.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import java.util.Set;

@Convert
@Slf4j
public class SetStringConverter implements AttributeConverter<Set<String>, String> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Set<String> attribute) {
        String result = null;
        if (attribute != null) {
            try {
                result = OBJECT_MAPPER.writeValueAsString(attribute);
            } catch (JsonProcessingException e) {
                log.error("couldn't convert Set<String> to String: {}", e.getMessage());
            }
        }
        return result;
    }

    @Override
    public Set<String> convertToEntityAttribute(String dbData) {
        Set<String> result = null;
        if (!StringUtils.isEmpty(dbData)) {
            try {
                result = OBJECT_MAPPER.readValue(dbData, new TypeReference<Set<String>>() {
                });
            } catch (JsonProcessingException e) {
                log.error("couldn't convert String to Set<String>: {}", e.getMessage());
            }
        }
        return result;
    }
}
