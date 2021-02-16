package io.rocketbase.commons.model.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.rocketbase.commons.model.user.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import java.util.Set;

@Convert
@Slf4j
public class SetPhoneNumberConverter implements AttributeConverter<Set<PhoneNumber>, String> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Set<PhoneNumber> attribute) {
        String result = null;
        if (attribute != null) {
            try {
                result = OBJECT_MAPPER.writeValueAsString(attribute);
            } catch (JsonProcessingException e) {
                log.error("couldn't convert PhoneNumbers to String: {}", e.getMessage());
            }
        }
        return result;
    }

    @Override
    public Set<PhoneNumber> convertToEntityAttribute(String dbData) {
        Set<PhoneNumber> result = null;
        if (!StringUtils.isEmpty(dbData)) {
            try {
                result = OBJECT_MAPPER.readValue(dbData, new TypeReference<Set<PhoneNumber>>() {
                });
            } catch (JsonProcessingException e) {
                log.error("couldn't convert String to PhoneNumbers: {}", e.getMessage());
            }
        }
        return result;
    }
}
