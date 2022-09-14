package com.example.study.api.result;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Stream;

public record Results<T>(
        boolean success,
        List<T> body,
        Boolean hasNext,
        String message) {

    public static <T> Results<T> success(List<T> body) {
        return new Results<>(true, body, false, null);
    }

    public static <T> Results<T> successWithHasNext(List<T> body) {
        return new Results<>(true, body, true, null);
    }

    public static <T> Results<T> failure(String message) {
        return new Results<>(false, null, null, message);
    }

    public boolean failure() {
        return !success;
    }

    public Stream<T> stream() {
        if (failure()) throw new IllegalStateException(message);
        if (body == null) return Stream.empty();
        return body.stream();
    }

    public List<T> get() {
        if (failure()) throw new IllegalStateException(message);
        return body;
    }

    public static <T> List<T> get(Results<T> results) {
        if (results == null) throw new IllegalStateException("통신 결과가 불안정합니다.");
        return results.get();
    }

    public static <T> List<T> get(Mono<Results<T>> resultsMono) {
        if (resultsMono == null) throw new IllegalStateException("통신이 불안정합니다.");
        return get(resultsMono.block());
    }

}
