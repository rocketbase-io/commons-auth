package io.rocketbase.commons.service;

import org.springframework.data.mongodb.core.query.Criteria;

public interface MongoQueryHelper {

    default Criteria buildRegexCriteria(String where, String text) {
        String pattern = text.trim() + "";
        if (!pattern.contains(".*")) {
            pattern = ".*" + pattern + ".*";
        }
        return Criteria.where(where).regex(pattern, "i");
    }

}
