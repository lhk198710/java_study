package com.example.study.api.payment.model.request;

import com.example.study.api.payment.common.constant.PgType;

public record PaymentCancelRequest(String orderId,
                                   CancelRequest cancelRequest,
                                   PgType pgType) {
}
