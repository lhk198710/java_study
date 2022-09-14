package com.example.study.api.payment.entity;

import com.example.study.api.payment.common.constant.PayType;
import com.example.study.api.payment.common.constant.PgType;
import com.example.study.api.payment.common.constant.StatusType;
import com.example.study.api.payment.iamport.model.Payment;
import com.example.study.api.payment.model.request.PaymentCancelRequest;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table(schema = "payment", name = "lf_pay_data")
public record LfPayData(@Id Long lfPayDataId,
                        String orderID,
                        String impUid,
                        String memberSrl,
                        String userKey,
                        String pgProvider,
                        String embPgProvider,
                        String pgTid,
                        String payMethod,
                        long totalAmount,
                        long cancelAmount,
                        long remainAmount,
                        String refundHolder,
                        String refundBankCode,
                        String refundAccount,
                        String cardName,
                        String cardNumber,
                        String cardCode,
                        String cardQuota,
                        String bankCode,
                        String bankName,
                        String receiptUrl,
                        String vbankCode,
                        String vbankName,
                        String vbankNum,
                        String vbankHolder,
                        String vbankDate,
                        String vbankIssuedAt,
                        String paidAt,
                        String cancelledAt,
                        String status,
                        @CreatedDate LocalDateTime createdAt,
                        @LastModifiedDate LocalDateTime updatedAt) {

    /**
     * 아임포트 결제 후 LF 결제 원장 데이터 생성
     *
     * @param payment
     */
    public static LfPayData makeLfPayData(Payment payment) {
        String status = "";

        status = switch(payment.status()) {
            case "ready" :
                if(payment.pay_method().equals(PayType.findIamPortName("VBANK"))) {
                    yield StatusType.WD.name();
                }
            case "paid" :
                yield StatusType.AV.name();
            case "cancelled" :
                yield StatusType.CN.name();
            case "failed" :
                yield StatusType.FL.name();
            default:
                yield StatusType.AV.name();
        };

        return new LfPayData(null, payment.merchant_uid(), payment.imp_uid(), null, null,
                PgType.findByIamPortPgName(payment.pg_provider()).name(),
                payment.emb_pg_provider() != null ? PgType.findByIamPortPgName(payment.emb_pg_provider()).name() : null,
                payment.pg_tid(), payment.pay_method(), payment.amount(), 0L, payment.amount(),
                null, null, null,
                payment.card_name(), payment.card_number(), payment.card_code(), payment.card_quota(),
                payment.bank_code(), payment.bank_name(), payment.receipt_url(),
                payment.vbank_code(), payment.vbank_name(), payment.vbank_num(), payment.vbank_holder(), payment.vbank_date(), payment.vbank_issued_at(), payment.paid_at(), payment.cancelled_at(),
                status, LocalDateTime.now(), LocalDateTime.now());
    }


    /**
     * 아임포트 취소 후 LF 결제 원장 취소 데이터 생성
     * @param paymentCancelRequest
     * @param payment
     * @return
     */
    public static LfPayData makeLfPayCancelData(PaymentCancelRequest paymentCancelRequest, Payment payment, StatusType statusType) {
        /**
         * 최초 1300원 결제
         *  1회차 300원 취소 시 payment.amount = 1300, payment.cancelamount = 300
         *  2회차 200원 취소 시 payment.amount = 1300, payment.cancelamount = 500
         *  이 후 전체 취소 시 payment.amount = 1300, payment.cancelamount = 1300
         */
        String refundHolder = "";
        String refundBankCode = "";
        String refundAccount = "";

        if(paymentCancelRequest != null) {
            refundHolder = paymentCancelRequest.cancelRequest().refundHolder();
            refundBankCode = paymentCancelRequest.cancelRequest().refundBankCode();
            refundAccount = paymentCancelRequest.cancelRequest().refundAccount();
        }

        return new LfPayData(null, payment.merchant_uid(), payment.imp_uid(), null, null,
                PgType.findByIamPortPgName(payment.pg_provider()).name(),
                payment.emb_pg_provider() != null ? PgType.findByIamPortPgName(payment.emb_pg_provider()).name() : null,
                payment.pg_tid(),
                payment.pay_method(), payment.amount(), payment.cancel_amount(), payment.amount() - payment.cancel_amount(),
                refundHolder, refundBankCode, refundAccount,
                payment.card_name(), payment.card_number(), payment.card_code(), payment.card_quota(),
                payment.bank_code(), payment.bank_name(), payment.receipt_url(),
                payment.vbank_code(), payment.vbank_name(), payment.vbank_num(), payment.vbank_holder(), payment.vbank_date(), payment.vbank_issued_at(), payment.paid_at(), payment.cancelled_at(),
                statusType.name(), LocalDateTime.now(), LocalDateTime.now());
    }


    /**
     * 아임포트 취소 요청을 진행하지 않은 상태에서 취소 데이터 입력 시 사용
     * @param paymentCancelRequest
     * @param lfPayData
     * @param statusType
     * @return
     */
    public static LfPayData makeLfPayCancelData(PaymentCancelRequest paymentCancelRequest, LfPayData lfPayData, StatusType statusType) {
        /**
         * 최초 1300원 결제
         *  1회차 300원 취소 시 payment.amount = 1300, payment.cancelamount = 300
         *  2회차 200원 취소 시 payment.amount = 1300, payment.cancelamount = 500
         *  이 후 전체 취소 시 payment.amount = 1300, payment.cancelamount = 1300
         */

        String refundHolder = paymentCancelRequest.cancelRequest().refundHolder();
        String refundBankCode = paymentCancelRequest.cancelRequest().refundBankCode();
        String refundAccount = paymentCancelRequest.cancelRequest().refundAccount();

        long cancelAmount = 0L;
        long remainAmount = 0L;

        if(lfPayData.status.equals("WD")) {
            cancelAmount = lfPayData.remainAmount;
            remainAmount = 0L;
        } else {
            cancelAmount = lfPayData.cancelAmount() + paymentCancelRequest.cancelRequest().cancelRequestAmount();
            remainAmount = lfPayData.remainAmount() - paymentCancelRequest.cancelRequest().cancelRequestAmount();
        }

        return new LfPayData(null, lfPayData.orderID(), lfPayData.impUid(), null, null,
                lfPayData.pgProvider(),
                lfPayData.embPgProvider(),
                lfPayData.pgTid(),
                lfPayData.payMethod(), lfPayData.totalAmount(), cancelAmount, remainAmount,
                refundHolder, refundBankCode, refundAccount,
                lfPayData.cardName(), lfPayData.cardNumber(), lfPayData.cardCode(), lfPayData.cardQuota(),
                lfPayData.bankCode(), lfPayData.bankName(), lfPayData.receiptUrl(),
                lfPayData.vbankCode(), lfPayData.vbankName(), lfPayData.vbankNum(), lfPayData.vbankHolder(), lfPayData.vbankDate(), lfPayData.vbankIssuedAt(), lfPayData.paidAt(), lfPayData.cancelledAt(),
                statusType.name(), LocalDateTime.now(), LocalDateTime.now());
    }
}
