package com.example.study.api.payment.service;

import com.example.study.api.payment.common.constant.PayType;
import com.example.study.api.payment.common.constant.StatusType;
import com.example.study.api.payment.common.exception.ErrorMessages;
import com.example.study.api.payment.common.exception.LFException;
import com.example.study.api.payment.entity.LfPayData;
import com.example.study.api.payment.entity.PgPayData;
import com.example.study.api.payment.iamport.IamportApi;
import com.example.study.api.payment.model.request.NotificationRequest;
import com.example.study.api.payment.repository.LfPayDataRepository;
import com.example.study.api.payment.repository.PgPayDataRepository;
import com.example.study.api.payment.common.constant.IamportStatusConstant;
import com.example.study.api.payment.iamport.model.AccessToken;
import com.example.study.api.payment.iamport.model.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotiService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotiService.class);

    private PgPayDataRepository pgPayDataRepository;
    private LfPayDataRepository lfPayDataRepository;
    private IamportApi iamportApi;
    private PaymentCancelService paymentCancelService;
    private PaymentService paymentService;

    public NotiService(PgPayDataRepository pgPayDataRepository, LfPayDataRepository lfPayDataRepository, IamportApi iamportApi, PaymentCancelService paymentCancelService, PaymentService paymentService) {
        this.pgPayDataRepository = pgPayDataRepository;
        this.lfPayDataRepository = lfPayDataRepository;
        this.iamportApi = iamportApi;
        this.paymentCancelService = paymentCancelService;
        this.paymentService = paymentService;
    }

    public int setNotiData(NotificationRequest notificationRequest) {
        LOGGER.info("notiRequest : {}", notificationRequest);

        /**
         * notificationRequest ????????? ????????? cancelled, paid, failed, ready status
         * ?????? ?????? ??????(?????? ??? ????????????)
         *  - ?????? ???????????? ?????? ??????
         *  - ?????? ??????????????? ?????? ?????? ?????? ?????? ?????? ?????? ??? ???
         */
        int result = 0;

        // 1. ???????????? ????????? ??????
        Payment payment = getPaymentData(notificationRequest.imp_uid());

        if (payment.pay_method().equals(PayType.findIamPortName("VBANK"))) {
            String status = notificationRequest.status();

            result = switch (status) {
                case "ready":
                    // ???????????? ????????? ?????????????????? ????????????
                    // ?????? DB??? ??????????????? ???????????? ?????? ???????????? ??????...?????? ????????? ?????? ????????????.
                    yield 0;
                case "paid":
                    PgPayData lastPgPayData = getLastPgPayData(notificationRequest.merchant_uid());
                    LfPayData lastLfPayData = getLastLfPayData(notificationRequest.merchant_uid());

                    validatePaidStatus(lastPgPayData, lastLfPayData, payment);
                    insertData(payment);

                    yield 1;
                case "cancelled":
                    insertData(payment);
                    yield 1;
                default:
                    // failed
                    yield 0;
            };
        } else {
            if(notificationRequest.status().equals("cancelled")) {
                insertData(payment);
                result = 1;
            }
        }

        return result;
    }

    private void insertData(Payment payment) {
        if (payment.status().equals("cancelled")) {
            paymentCancelService.insertPgPayData(PgPayData.ofNew(payment, LocalDateTime.now(), LocalDateTime.now()));
            paymentCancelService.insertLfPayDataWithPayment(null, payment, payment.amount() != payment.cancel_amount() ? StatusType.PC : StatusType.CN);
        } else {
            paymentService.insertPgPayData(PgPayData.ofNew(payment, LocalDateTime.now(), LocalDateTime.now()));
            paymentService.insertLfPayData(LfPayData.makeLfPayData(payment));
        }
    }

    /**
     * ???????????? ?????? ????????? ?????? API ??????
     *
     * @param imp_uid
     * @return
     */
    private Payment getPaymentData(String imp_uid) {
        AccessToken accessToken = iamportApi.getToken();
        Payment payment = iamportApi.getPaymentData(accessToken.access_token(), imp_uid);

        return payment;
    }

    private void validatePaidStatus(PgPayData pgPayData, LfPayData lfPayData, Payment payment) {
        if(pgPayData.status().equals("AV") && lfPayData.status().equals("AV")) {
            throw new LFException(ErrorMessages.CODE_3606);
        }

        // ??????????????? ?????? ?????? ??????
        if (pgPayData.payMethod().equals(PayType.findIamPortName("VBANK")) && lfPayData.payMethod().equals(PayType.findIamPortName("VBANK")) && payment.pay_method().equals(PayType.findIamPortName("VBANK"))) {
            // 1. status ???????????? ?????? ??????
            if (pgPayData.status().equals("paid") && lfPayData.status().equals(StatusType.AV.name())) {
                throw new LFException(ErrorMessages.CODE_3311);
            }

            // 2. ???????????? ???????????? ?????? ??????
            if (!payment.imp_uid().equals(lfPayData.impUid()) || !payment.imp_uid().equals(pgPayData.impUid())) {
                throw new LFException(ErrorMessages.CODE_3601);
            }
        }
    }

    private PgPayData findTopByMerchantUidOrderByPgPayDataIDDesc(String orderId) {
        return pgPayDataRepository.findTopByMerchantUidOrderByPgPayDataIDDesc(orderId);
    }

    private LfPayData findTopByOrderIDOrderByLfPayDataIdDesc(String orderId) {
        return lfPayDataRepository.findTopByOrderIDOrderByLfPayDataIdDesc(orderId);
    }

    private PgPayData getLastPgPayData(String orderId) {
        PgPayData pgPayData = findTopByMerchantUidOrderByPgPayDataIDDesc(orderId);

        if (pgPayData == null) {
            throw new LFException(ErrorMessages.CODE_3601);
        }

        return pgPayData;
    }

    private LfPayData getLastLfPayData(String orderId) {
        LfPayData lfPayData = findTopByOrderIDOrderByLfPayDataIdDesc(orderId);

        if (lfPayData == null) {
            throw new LFException(ErrorMessages.CODE_3601);
        }

        return lfPayData;
    }
}
