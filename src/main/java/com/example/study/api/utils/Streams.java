package com.example.study.api.utils;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toUnmodifiableMap;

public class Streams {

    public static <E> Stream<E> of(Collection<E> collection) {
        if (collection == null || collection.isEmpty()) {
            return Stream.empty();
        }

        return collection.stream();
    }

    public static <E> int sum(Collection<E> collection, ToIntFunction<E> mapper) {
        if (collection == null || collection.isEmpty()) {
            return 0;
        }

        return of(collection).mapToInt(mapper).sum();
    }

    public static int sum(Collection<Integer> collection) {
        return of(collection).mapToInt(Integer::intValue).sum();
    }

    public static <E, R> Set<R> toSet(
            Collection<E> collection,
            Function<E, R> mapper) {

        return of(collection)
                .map(mapper)
                .collect(Collectors.toUnmodifiableSet());
    }

    public static <E, R> List<R> toList(
            Collection<E> collection,
            Function<E, R> mapper) {

        return of(collection)
                .map(mapper).toList();
    }

    public static <TK, TV, RK, RV> Map<RK, RV> toMap(
            Map<TK, TV> map,
            Function<TK, RK> keyMapper,
            Function<TV, RV> valueMapper) {

        if (map == null || map.isEmpty()) {
            return Collections.emptyMap();
        }

        return map.entrySet().stream().collect(
                toUnmodifiableMap(
                        e -> keyMapper.apply(e.getKey()),
                        e -> valueMapper.apply(e.getValue()),
                        (a, b) -> a));
    }

    public static <K, V, R> Map<K, R> toMap(
            Map<K, V> map,
            Function<V, R> valueMapper) {

        if (map == null || map.isEmpty()) {
            return Collections.emptyMap();
        }

        return map.entrySet().stream()
                .collect(
                        toUnmodifiableMap(
                                Map.Entry::getKey,
                                e -> valueMapper.apply(e.getValue()),
                                (a, b) -> a));
    }


    public static <E, K> Map<K, E> toMap(
            Collection<E> collection,
            Function<E, K> keyMapper) {

        return toMap(collection, keyMapper, Function.identity());
    }

    public static <E, K, V> Map<K, V> toMap(
            Collection<E> collection,
            Function<E, K> keyMapper,
            Function<E, V> valueMapper) {

        if (collection == null || collection.isEmpty()) {
            return Collections.emptyMap();
        }

        return collection.stream()
                .collect(toUnmodifiableMap(keyMapper, valueMapper, (a, b) -> a));
    }

    public static <E, K> Map<K, Integer> toMapWithInit(
            Collection<E> collection,
            Function<E, K> keyMapper,
            Integer initValue) {

        return toMap(collection, keyMapper, e -> initValue);
    }

    public static <E, K, V> Map<K, V> toMapWithInit(
            Collection<E> collection,
            Function<E, K> keyMapper,
            Supplier<V> supplier) {

        return toMap(collection, keyMapper, e -> supplier.get());
    }

    public static <K, V, R> List<R> toList(Map<K, V> items, BiFunction<K, V, R> mapper) {
        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }

        return items.entrySet().stream()
                .map(e -> mapper.apply(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    public static <E> E anyOne(Collection<E> collection, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new NoSuchElementException(message);
        }

        return collection.stream()
                .findAny()
                .orElseThrow(() -> new NoSuchElementException(message));
    }

    public static <E> E anyOne(Collection<E> collection) {
        return anyOne(collection, "항목없음");
    }

    public static <E> E anyOne(
            Collection<E> collection,
            Predicate<E> filter,
            String message) {
        if (collection == null || collection.isEmpty()) {
            throw new NoSuchElementException(message);
        }

        return collection.stream()
                .filter(filter)
                .findAny()
                .orElseThrow(() -> new NoSuchElementException(message));
    }

}
