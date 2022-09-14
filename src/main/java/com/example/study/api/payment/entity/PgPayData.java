package com.example.study.api.payment.entity;

import com.example.study.api.payment.iamport.model.Payment;
import com.example.study.api.payment.iamport.model.PaymentCancelDetails;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Set;

@Table(schema = "payment", name = "pg_pay_data")
public record PgPayData(@Id Long pgPayDataID,
                        String impUid,
                        String merchantUid,
                        String payMethod,
                        String channel,
                        String pgProvider,
                        String embPgProvider,
                        String pgTid,
                        int escrow,
                        String applyNum,
                        String bankCode,
                        String bankName,
                        String cardCode,
                        String cardName,
                        String cardNumber,
                        String cardQuota,
                        String cardType,
                        String vbankCode,
                        String vbankName,
                        String vbankNum,
                        String vbankHolder,
                        String vbankDate,
                        String vbankIssuedAt,
                        String name,
                        Long amount,
                        Long cancelAmount,
                        String currency,
                        String buyerName,
                        String buyerEmail,
                        String buyerTel,
                        String buyerAddr,
                        String buyerPostcode,
                        String customData,
                        String status,
                        String startedAt,
                        String paidAt,
                        String failedAt,
                        String cancelledAt,
                        String failReason,
                        String cancelReason,
                        String receiptUrl,
                        @MappedCollection(idColumn = "lf_pay_data_id", keyColumn = "payment_cancel_detail_id") Set<PaymentCancelDetails> cancelHistory,
                        @CreatedDate LocalDateTime createdAt,
                        @LastModifiedDate LocalDateTime updatedAt) {

    public static PgPayData ofNew(Payment payment, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new PgPayData(null, payment.imp_uid(), payment.merchant_uid(), payment.pay_method(), payment.channel(), payment.pg_provider(), payment.emb_pg_provider(), payment.pg_tid(), payment.escrow() ? 1 : 0,
                payment.apply_num(), payment.bank_code(), payment.bank_name(), payment.card_code(), payment.card_name(), payment.card_number(), payment.card_quota(), payment.card_type(), payment.vbank_code(),
                payment.vbank_name(), payment.vbank_num(), payment.vbank_holder(), payment.vbank_date(), payment.vbank_issued_at(), payment.name(), payment.amount(), payment.cancel_amount(), payment.currency(),
                payment.buyer_name(), payment.buyer_email(), payment.buyer_tel(), payment.buyer_addr(), payment.buyer_postcode(), payment.custom_data(), payment.status(), payment.paid_at(),
                payment.cancelled_at(), payment.failed_at(), payment.cancelled_at(), payment.fail_reason(), payment.cancel_reason(), payment.receipt_url(), payment.cancel_history(),
                createdAt != null ? createdAt : LocalDateTime.now(), updatedAt != null ? updatedAt : LocalDateTime.now());
    }
}
