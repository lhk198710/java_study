package com.example.study.api.payment.model.request;

public record CancelRequest(String orderId,
                            Long cancelRequestAmount,
                            String reason,
                            String refundHolder,
                            String refundBankCode,
                            String refundAccount,
                            String refundTel) {
}

