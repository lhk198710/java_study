package com.example.study.api.payment.model.response;

public record PaymentCancelResponse(boolean success,
                                    String message,
                                    String orderId,
                                    Long canceled_amount) {
}
