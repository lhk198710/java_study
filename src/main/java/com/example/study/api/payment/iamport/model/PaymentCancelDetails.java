package com.example.study.api.payment.iamport.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("payment_cancel_details")
public record PaymentCancelDetails(@Id Long payment_cancel_detail_id,
                                   Long lf_pay_data_id,
                                   String pg_tid,
                                   Long amount,
                                   Long canceled_at,
                                   String reason,
                                   String receipt_url) {
}
