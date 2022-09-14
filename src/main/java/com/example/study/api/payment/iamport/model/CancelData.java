package com.example.study.api.payment.iamport.model;

public record CancelData(String imp_uid,
                         String merchant_uid,
                         Long amount,
                         Long tax_free,
                         Long checksum,
                         String reason,
                         String refund_holder,
                         String refund_bank,
                         String refund_account,
                         String escrow_confirmed
                         ) {
}
