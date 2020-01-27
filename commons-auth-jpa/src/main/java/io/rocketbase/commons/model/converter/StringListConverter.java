package io.rocketbase.commons.model.converter;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.springframework.util.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import java.util.ArrayList;
import java.util.List;

@Convert
public class StringListConverter implements AttributeConverter<List<String>, String> {

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        String result = null;
        if (attribute != null) {
            result = Joiner.on(";").join(attribute);
        }
        return result;
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        List<String> result = new ArrayList<>();
        if (!StringUtils.isEmpty(dbData)) {
            result = Lists.newArrayList(Splitter.on(";").split(dbData));
        }
        return result;
    }
}
