package com.example.study.api.payment.model.response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public record IamportPayResponse(String pg,
                                 String pay_method,
                                 boolean escrow,
                                 String merchant_uid,
                                 String name,
                                 long amount,
                                 IamportCustomData custom_data,
                                 long tax_free,
                                 String currency,
                                 String language,
                                 String m_redirect_url,
                                 String notice_url,
                                 boolean digital,
                                 String vbank_due,
                                 String app_scheme,
                                 String biz_num,
                                 String buyer_email,
                                 String buyer_name){


    public static IamportPayResponse makeIamportPayResponse(String pg, String pay_method, long amount, long tax_free, String orderId, String name) {
        return new IamportPayResponse(pg, pay_method, false, orderId, name, amount, null, tax_free, "KRW", "ko",
                "http://localhost:18083/view/v1/payments/iamportRedirect",
                "http://localhost:18083/api/v1/payments/notification",
                false,
                LocalDateTime.now().plus(2, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS).minusSeconds(1).format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")),
                null, null, "", "테스트_사용자");
    }
}