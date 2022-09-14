package com.example.study.api.result;

import reactor.core.publisher.Mono;

public record Result<T>(
        boolean success,
        T body,
        String message) {

    public static <T> Result<T> successWithoutBody() {
        return new Result<>(true, null, null);
    }

    public static <T> Result<T> success(T body) {
        return new Result<>(true, body, null);
    }

    public static <T> Result<T> failure(String message) {
        return new Result<>(false, null, message);
    }

    public boolean failure() {
        return !success;
    }

    public T get() {
        if (failure()) throw new IllegalStateException(message);
        return body;
    }

    public static <T> T get(Result<T> result) {
        if (result == null) throw new IllegalStateException("통신 결과가 불안정합니다.");
        return result.get();
    }

    public static <T> T get(Mono<Result<T>> resultMono) {
        if (resultMono == null) throw new IllegalStateException("통신이 불안정합니다.");
        return get(resultMono.block());
    }
}
