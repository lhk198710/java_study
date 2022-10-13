package com.example.study.api.payment.iamport.model;

import java.util.Set;

/**
 * 아임포트 결제 & 취소 데이터
 * @param imp_uid
 * @param merchant_uid
 * @param pay_method
 * @param channel
 * @param pg_provider
 * @param emb_pg_provider
 * @param pg_tid
 * @param escrow
 * @param apply_num
 * @param bank_code
 * @param bank_name
 * @param card_code
 * @param card_name
 * @param card_number
 * @param card_quota
 * @param card_type
 * @param vbank_code
 * @param vbank_name
 * @param vbank_num
 * @param vbank_holder
 * @param vbank_date
 * @param vbank_issued_at
 * @param name
 * @param amount
 * @param cancel_amount
 * @param currency
 * @param buyer_name
 * @param buyer_email
 * @param buyer_tel
 * @param buyer_addr
 * @param buyer_postcode
 * @param custom_data
 * @param status
 * @param started_at
 * @param paid_at
 * @param failed_at
 * @param cancelled_at
 * @param fail_reason
 * @param cancel_reason
 * @param receipt_url
 * @param cancel_history
 * @param cash_receipt_issued
 * @param customer_uid
 * @param customer_uid_usage
 */
public record Payment(String imp_uid,
                      String merchant_uid,
                      String pay_method,
                      String channel,
                      String pg_provider,
                      String emb_pg_provider,
                      String pg_tid,
                      String pg_id,
                      boolean escrow,
                      String apply_num,
                      String bank_code,
                      String bank_name,
                      String card_code,
                      String card_name,
                      String card_number,
                      String card_quota,
                      String card_type,
                      String vbank_code,
                      String vbank_name,
                      String vbank_num,
                      String vbank_holder,
                      String vbank_date,
                      String vbank_issued_at,
                      String name,
                      Long amount,
                      Long cancel_amount,
                      String currency,
                      String buyer_name,
                      String buyer_email,
                      String buyer_tel,
                      String buyer_addr,
                      String buyer_postcode,
                      String custom_data,
                      String user_agent,
                      String status,
                      String started_at,
                      String paid_at,
                      String failed_at,
                      String cancelled_at,
                      String fail_reason,
                      String cancel_reason,
                      String receipt_url,
                      Set<PaymentCancelDetails> cancel_history,
                      boolean cash_receipt_issued,
                      String customer_uid,
                      String customer_uid_usage) {
}
