package com.example.study.api.payment.iamport.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("payment_cancel_details")
public record PaymentCancelDetails(@Id long payment_cancel_detail_id,
                                   long lf_pay_data_id,
                                   String pg_tid,
                                   long amount,
                                   long canceled_at,
                                   String reason,
                                   String receipt_url) {
}
