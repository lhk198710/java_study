package com.example.study.api.payment.model.request;
/**
 * 아임포트 결제창 호출을 위해 클라이언트에서 서버에 전달하는 데이터
 * @param pay_method
 * @param pg
 * @param amount
 * @param name
 * @param orderId
 */
public record IamportPayRequest(String pay_method,
                                String pg,
                                long amount,
                                long tax_free,
                                String name,
                                String orderId) {
}
