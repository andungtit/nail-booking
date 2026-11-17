package com.nailroom.booking.util;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.Collection;

public final class FilterUtils {
    private FilterUtils() {}

    public static <E, V> Specification<E> eq(String field, V value) {
        return (value == null || (value instanceof String s && s.isBlank())) ? null
                : (root, q, cb) -> cb.equal(getPath(root, field), value);
    }

    public static <E, V> Specification<E> in(String field, Collection<V> values) {
        return (values == null || values.isEmpty()) ? null
                : (root, q, cb) -> getPath(root, field).in(values);
    }

    public static <E> Specification<E> like(String field, String keyword) {
        return (keyword == null || keyword.isBlank()) ? null
                : (root, q, cb) -> cb.like(cb.lower(getPath(root, field).as(String.class)),
                "%" + keyword.trim().toLowerCase() + "%");
    }

    public static <E, V extends Comparable<? super V>> Specification<E> gte(String field, V value) {
        return value == null ? null
                : (root, q, cb) -> cb.greaterThanOrEqualTo(getPath(root, field), value);
    }

    public static <E, V extends Comparable<? super V>> Specification<E> lte(String field, V value) {
        return value == null ? null
                : (root, q, cb) -> cb.lessThanOrEqualTo(getPath(root, field), value);
    }

    public static <E> Specification<E> anyLike(String keyword, String... fields) {
        if (keyword == null || keyword.isBlank()) return null;
        String pattern = "%" + keyword.trim().toLowerCase() + "%";
        return (root, q, cb) -> {
            Predicate[] predicates = Arrays.stream(fields)
                    .map(f -> cb.like(cb.lower(getPath(root, f).as(String.class)), pattern))
                    .toArray(Predicate[]::new);
            return cb.or(predicates);
        };
    }

    @SafeVarargs
    public static <E> Specification<E> allOf(Specification<E>... specs) {
        return Specification.allOf(specs);
    }

    @SuppressWarnings("unchecked")
    private static <Y> Path<Y> getPath(Path<?> root, String field) {
        Path<?> p = root;
        for (String part : field.split("\\.")) {
            p = p.get(part);
        }
        return (Path<Y>) p;
    }
}
