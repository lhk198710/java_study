package com.example.study.api.payment.model.response;

/**
 * PayAuthViewResponse : 아임포트 결제창 호출을 위한 데이터 응답
 *  Created by lhk8710
 */
    public record PaymentAuthViewResponse(String pg,
                                      String pay_method,
                                      boolean escrow,
                                      String merchant_uid,
                                      String name,
                                      Long amount,
                                      String customData,
                                      Long taxFree,
                                      String currency,
                                      String language,
                                      String buyerName,
                                      String buyerTel,
                                      String buyerEmail,
                                      String buyerAddr,
                                      String buyerPostcode,
                                      String confirmUrl,
                                      String noticeUrl,
                                      String display,
                                      boolean digital,
                                      String vbankDue,
                                      String mRedirectUrl,
                                      String appSchemem,
                                      String bizNum) {
}
