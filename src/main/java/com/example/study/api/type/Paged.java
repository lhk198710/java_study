package com.example.study.api.type;

import com.lf.c.util.collection.Streams;

import java.util.List;
import java.util.function.Function;

public record Paged<T>(
        int totalElements,
        int totalPages,
        List<T> contents) {

    public static <T> Paged<T> of(int totalElements, int totalPages, List<T> contents) {
        return new Paged<>(totalElements, totalPages, contents);
    }

    public static <T> Paged<T> of(long totalElements, int totalPages, List<T> contents) {
        return of((int) totalElements, totalPages, contents);
    }

    public <R> Paged<R> map(Function<T, R> mapper) {
        return new Paged<>(totalElements, totalPages, Streams.toList(contents, mapper));
    }
}
