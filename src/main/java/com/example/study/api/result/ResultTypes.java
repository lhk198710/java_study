package com.example.study.api.result;

import org.springframework.core.ParameterizedTypeReference;

public abstract class ResultTypes {
    public static ParameterizedTypeReference<Result<Void>> VOID = new ParameterizedTypeReference<>() {};
    public static ParameterizedTypeReference<Result<String>> STRING = new ParameterizedTypeReference<>() {};
    public static ParameterizedTypeReference<Result<Integer>> INTEGER = new ParameterizedTypeReference<>() {};
    public static ParameterizedTypeReference<Result<Long>> LONG = new ParameterizedTypeReference<>() {};
}
