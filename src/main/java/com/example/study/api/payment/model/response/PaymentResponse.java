package com.example.study.api.payment.model.response;

public record PaymentResponse(boolean success,
                              String message,
                              String orderId,
                              Long totalAmount,
                              String payMethod,
                              String pg) {
}
