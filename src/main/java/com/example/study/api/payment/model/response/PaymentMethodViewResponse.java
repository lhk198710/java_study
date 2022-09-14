package com.example.study.api.payment.model.response;

import java.util.Set;

/**
 * PaymentViewResponse : 결제수단 응답 값 중 PG 키 값 포함
 *  Created by lhk8710
 * @param paymentMethodViewRespons : 결제수단 응답 값 중 PG 데이터 해당
 */
public record PaymentMethodViewResponse(Set<PaymentPgMethodViewResponse> paymentMethodViewRespons) {
}
