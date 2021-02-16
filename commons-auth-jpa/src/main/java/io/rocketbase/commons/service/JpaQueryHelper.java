package io.rocketbase.commons.service;

import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.List;

public interface JpaQueryHelper {

    default String buildLikeString(String value) {
        if (value.contains("*") || value.contains("%")) {
            return value.trim().toLowerCase().replace("*", "%");
        }
        return "%" + value.trim().toLowerCase() + "%";
    }

    default void addToListIfNotEmpty(List<Predicate> list, String value, Path path, CriteriaBuilder cb) {
        if (!StringUtils.isEmpty(value)) {
            list.add(cb.like(cb.lower(path), buildLikeString(value)));
        }
    }
}
