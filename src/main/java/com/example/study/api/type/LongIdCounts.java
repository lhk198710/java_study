package com.example.study.api.type;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public record LongIdCounts(
        Map<Long, Integer> idCounts) {

    public int count(long id) {
        if (idCounts == null || !idCounts.containsKey(id)) {
            return 0;
        }
        return idCounts.get(id);
    }

    public Set<Long> ids() {
        return idCounts != null
                ? idCounts.keySet()
                : Collections.emptySet();
    }

    public int sum() {
        if (idCounts == null) {
            return 0;
        }

        return idCounts.values().stream()
                .mapToInt(i -> i)
                .sum();
    }

    public int size() {
        return idCounts.size();
    }

    public Integer get(Long id) {
        return Optional.of(idCounts.get(id))
                .orElseThrow(() -> new IllegalArgumentException("해당 값(" + id + ")이 없습니다."));
    }

    public Integer or(Long id, Integer defaultValue) {
        return Optional.of(idCounts.get(id))
                .orElse(defaultValue);
    }
}
