package com.example.study.api.payment.iamport.model;

public record IamportResponse<T>(int code,
                                 String message,
                                 T response) {
}
