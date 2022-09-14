package com.example.study.api.payment.model.response;

/**
 * PaymentPgViewResponse : 결제수단 응답 값 중 PG 키 값 포함
 *  Created by lhk8710
 * @param paymentKey : 아임포트에 전송 할 결제수단
 * @param paymentName : 결제수단 이름
 * @param paymentPg : 아임포트에 전송 할 pg key
 */
public record PaymentPgMethodViewResponse(String paymentKey,
                                          String paymentName,
                                          String paymentPg) {
}
