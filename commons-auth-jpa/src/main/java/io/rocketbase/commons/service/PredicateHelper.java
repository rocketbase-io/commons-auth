package io.rocketbase.commons.service;

import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public interface PredicateHelper {

    default String buildLikeString(String value) {
        if (value.contains("*") || value.contains("%")) {
            return value.trim().toLowerCase().replace("*", "%");
        }
        return "%" + value.trim().toLowerCase() + "%";
    }

    default void addToListIfNotEmpty(List<Predicate> list, String value, String path, Root root, CriteriaBuilder cb) {
        if (!StringUtils.isEmpty(value)) {
            list.add(cb.like(cb.lower(root.get(path)), buildLikeString(value)));
        }
    }
}
