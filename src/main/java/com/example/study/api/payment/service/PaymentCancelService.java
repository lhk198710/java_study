package com.example.study.api.payment.service;

import com.example.study.api.payment.common.constant.PayType;
import com.example.study.api.payment.common.constant.StatusType;
import com.example.study.api.payment.common.exception.ErrorMessages;
import com.example.study.api.payment.common.exception.LFException;
import com.example.study.api.payment.entity.BankCode;
import com.example.study.api.payment.entity.PgPayData;
import com.example.study.api.payment.iamport.IamportApi;
import com.example.study.api.payment.iamport.model.CancelData;
import com.example.study.api.payment.model.request.PaymentCancelRequest;
import com.example.study.api.payment.model.response.PaymentCancelResponse;
import com.example.study.api.payment.entity.LfPayData;
import com.example.study.api.payment.iamport.model.AccessToken;
import com.example.study.api.payment.iamport.model.Payment;
import com.example.study.api.payment.repository.BankCodeRepository;
import com.example.study.api.payment.repository.LfPayDataRepository;
import com.example.study.api.payment.repository.PgPayDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class PaymentCancelService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentCancelService.class);

    private PgPayDataRepository pgPayDataRepository;
    private LfPayDataRepository lfPayDataRepository;
    private BankCodeRepository bankCodeRepository;
    private IamportApi iamportApi;

    public PaymentCancelService(PgPayDataRepository pgPayDataRepository, LfPayDataRepository lfPayDataRepository, BankCodeRepository bankCodeRepository, IamportApi iamportApi) {
        this.pgPayDataRepository = pgPayDataRepository;
        this.lfPayDataRepository = lfPayDataRepository;
        this.bankCodeRepository = bankCodeRepository;
        this.iamportApi = iamportApi;
    }

    public PaymentCancelResponse paymentCancel(String orderId, PaymentCancelRequest paymentCancelRequest) {
        // ????????? ????????? ?????? ????????? ??????
        LfPayData lastLfPayData = this.getLastLfPayData(orderId);

        // ???????????? ???????????? ?????? ?????? ????????? ??????
        Payment orgData = getPaymentData(lastLfPayData.impUid());

        // Req??? ????????? ???????????? ????????? ????????? ????????? ??????
        validateIamportBackendAndWebData(lastLfPayData, paymentCancelRequest);

        StatusType statusType = setCancelStatus(orgData, paymentCancelRequest, lastLfPayData);

        // ???????????? ?????? ???????????? ??????
        boolean checkIamportCancelable = checkIamportCancelable(orgData, paymentCancelRequest, lastLfPayData);

        if(checkIamportCancelable) {
            // ???????????? ?????? ?????? ????????? ??????
            CancelData cancelData = this.convertPgPayDataToIamportCancelData(orgData, lastLfPayData, paymentCancelRequest);

            try {
                // ???????????? ?????? ??????
                Payment payment = this.cancel(cancelData);

                // ????????? ??????(???????????? ?????? ?????? ???)
                PgPayData insertData = PgPayData.ofNew(payment, LocalDateTime.now(), LocalDateTime.now());

                this.insertPgPayData(insertData);
                this.insertLfPayDataWithPayment(paymentCancelRequest, payment, statusType);
            } catch (LFException le) {
                le.getMessage();
                throw le;
                // ???????????? API ?????? ??????  : ????????? ???????????? ???????????? ????????? ?????? ???????????? ????????????.
                // ???????????? ?????? ????????? ?????? ?????? : code : 1, message : ?????????????????? ????????? ?????????????????????. (??????????????????(KRW) :  1,300.00 vs 900.00), response : null
                // DB??? ?????? ?????? ?????? ??? ???
            } catch (Exception ex) {
                ex.getMessage();
                throw ex;
                // ?????? ??????
                // ????????? ??????????????? DB ????????? ?????? ???.
                // ??? ????????? ?????? ?????? ??????
            }

        } else {
            // ????????? ??????
            this.insertLfPayDataWithoutPayment(paymentCancelRequest, lastLfPayData, statusType);
        }

        // ?????? ????????? ??????
        PaymentCancelResponse paymentCancelResponse = this.makePaymentCancelResponse(true, "?????? ??????", paymentCancelRequest);

        return paymentCancelResponse;
    }

    /**
     * ????????? ?????? ?????? ?????? ?????? ?????? ????????? ?????? Period ?????? ??????
     * @param payment
     * @return
     */
    public Period getPeriod(Payment payment) {
        LocalDate paidAtTime = LocalDate.ofInstant(Instant.ofEpochSecond(Long.parseLong(payment.started_at())), TimeZone.getDefault().toZoneId());
        LocalDate nowTime = LocalDate.now();
        Period period = Period.between(paidAtTime.withDayOfMonth(1), nowTime.withDayOfMonth(1));
        return period;
    }

    /**
     * ???????????? API ?????? ?????? ?????? ??????
     * @param payment
     * @param paymentCancelRequest
     * @param lastLfPayData
     * @return
     */
    public boolean checkIamportCancelable(Payment payment, PaymentCancelRequest paymentCancelRequest, LfPayData lastLfPayData) {
        boolean result = true;
        long cancelAmount = calculateCanceAmount(payment, paymentCancelRequest);

        Period period = getPeriod(payment);

        if(payment.pay_method().equals("phone")) {
            // ??????????????? ?????? ????????? ??? ?????? ??????????????? ?????? ???????????? ?????????.
            if(lastLfPayData.status().equals("PC")) {
                result = false;
            }

            // ????????? ?????? ?????? ??????
            if(cancelAmount != 0) {
                result = false;
            }

            // ????????? ?????? ?????? ??????
            if ((period.getMonths() > 0)) {
                result = false;
            }
        }

        // ???????????? ?????? ?????? ??? ??????
        if(payment.pay_method().equals(PayType.findIamPortName("VBANK")) && lastLfPayData.status().equals(StatusType.WD.name())) {
            result = false;
        }

        // ????????? ????????? ??????, ?????? ?????? ????????? ??? ??????
        if(lastLfPayData.status().equals(StatusType.RF.name()) || lastLfPayData.status().equals(StatusType.PR.name())) {
            result = false;
        }

        return result;
    }

    /**
     * ?????? ?????? ????????????
     * @param payment
     * @param paymentCancelRequest
     * @return
     */
    public long calculateCanceAmount(Payment payment, PaymentCancelRequest paymentCancelRequest) {
        return payment.amount() - payment.cancel_amount() - paymentCancelRequest.cancelRequest().cancelRequestAmount();
    }

    /**
     * Status ??????
     * @param payment
     * @param paymentCancelRequest
     * @param lastLfPayData
     * @return
     */
    public StatusType setCancelStatus(Payment payment, PaymentCancelRequest paymentCancelRequest, LfPayData lastLfPayData) {
        StatusType status = StatusType.CN;

        Period period = getPeriod(payment);
        long cancelAmount = calculateCanceAmount(payment, paymentCancelRequest);

        // ?????? ?????? ?????? ?????? ??????
        if(payment.amount() - payment.cancel_amount() > paymentCancelRequest.cancelRequest().cancelRequestAmount()) {
            status = StatusType.PC;
        }

        if (payment.pay_method().equals("phone")) {
            if ((period.getMonths() > 0)) {
                // 1. ?????? ?????? ??????
                if(cancelAmount == 0) {
                    status = StatusType.RF;
                } else {
                    status = StatusType.PR;
                }
            } else {
                // 2. ?????? ?????? ??????
                if(cancelAmount == 0) {
                    status = StatusType.CN;
                } else {
                    status = StatusType.PR;
                }
            }
        }

        if(payment.pay_method().equals(PayType.findIamPortName("VBANK")) && lastLfPayData.status().equals(StatusType.WD.name())) {
            if(cancelAmount == 0) {
                // ?????? ??????
                status = StatusType.VN;
            } else {
                // ?????? ??????
                // throw new LFException(ErrorMessages.CODE_3512);

                if(lastLfPayData.status().equals("WD")) {
                    status = StatusType.CN;
                } else {
                    // ?????? ??????(StatusType.PC)??? ???????????? ?????? ??????(StatusType.PR)??? ???????????? ???????????? ????????? ????????? ?????? ?????????
                    status = StatusType.PC;
                }
            }
        }

        return status;
    }

    private LfPayData findTopByOrderIDOrderByLfPayDataIdDesc(String orderId) {
        return lfPayDataRepository.findTopByOrderIDOrderByLfPayDataIdDesc(orderId);
    }

    /**
     * DB??? ????????? LF ????????? ??? ??????
     *
     * @param orderId
     * @return
     */
    private LfPayData getLastLfPayData(String orderId) {
        LfPayData lfPayData = findTopByOrderIDOrderByLfPayDataIdDesc(orderId);

        if (lfPayData == null) {
            throw new LFException(ErrorMessages.CODE_3601);
        }

        return lfPayData;
    }

    /**
     * ?????? ?????? ??? ?????? ??? ??????
     *
     * @param success
     * @param message
     * @param paymentCancelRequest
     * @return
     */
    private PaymentCancelResponse makePaymentCancelResponse(boolean success, String message, PaymentCancelRequest paymentCancelRequest) {
        return new PaymentCancelResponse(success, message, paymentCancelRequest.orderId(), paymentCancelRequest.cancelRequest().cancelRequestAmount());
    }

    /**
     * ???????????? ?????? ????????? DB ??????
     *
     * @param pgPayData
     */
    @Transactional
    public void insertPgPayData(PgPayData pgPayData) {
        pgPayDataRepository.save(pgPayData);
    }

    /**
     * lf_pay_data DB ??????
     *
     * @param paymentCancelRequest
     * @param payment
     */
    @Transactional
    public void insertLfPayDataWithPayment(PaymentCancelRequest paymentCancelRequest, Payment payment, StatusType statusType) {
        lfPayDataRepository.save(LfPayData.makeLfPayCancelData(paymentCancelRequest, payment, statusType));
    }

    @Transactional
    public void insertLfPayDataWithoutPayment(PaymentCancelRequest paymentCancelRequest, LfPayData lfPayData, StatusType statusType) {
        lfPayDataRepository.save(LfPayData.makeLfPayCancelData(paymentCancelRequest, lfPayData, statusType));
    }

    /**
     * ?????? ????????? ??????
     *
     * @param lfPayData
     * @param paymentCancelRequest
     */
    private void validateIamportBackendAndWebData(LfPayData lfPayData, PaymentCancelRequest paymentCancelRequest) {
        // 1. ???????????? ?????? ?????? ?????? ??????
        if (lfPayData == null || lfPayData.orderID() == null ||  lfPayData.orderID() == "") {
            throw new LFException(ErrorMessages.CODE_3601);
        }

        // 2. ?????? ??????
        long orderedTotalAmount = calculateTotalAmount(lfPayData);  // ???????????? ?????? ??????
        long cancelRequestAmount = paymentCancelRequest.cancelRequest().cancelRequestAmount(); // ?????????????????? ?????? ?????? ?????? ?????? ??????

        LOGGER.info("orderID : {}, orderedTotalAmount : {}, cancelRequestAmount : {}", lfPayData.orderID(), orderedTotalAmount, cancelRequestAmount);

        if (orderedTotalAmount == 0L) {
            throw new LFException(ErrorMessages.CODE_3605);
        }

        if (orderedTotalAmount < cancelRequestAmount) {
            throw new LFException(ErrorMessages.CODE_3503, String.valueOf(orderedTotalAmount));
        }

        // 4. ???????????? ?????? ?????? ?????? ?????? ?????? ??????
        LocalDate paidAtTime = LocalDate.ofInstant(Instant.ofEpochSecond(Long.parseLong(lfPayData.paidAt())), TimeZone.getDefault().toZoneId());
        LocalDate nowTime = LocalDate.now();

        Period period = Period.between(paidAtTime.withDayOfMonth(1), nowTime.withDayOfMonth(1));
        if ((period.getMonths() > 0) && lfPayData.payMethod().equals("phone")) {
            throw new LFException(ErrorMessages.CODE_3519);
        }
    }

    /**
     * ???????????? ?????? ?????? ????????? ??????
     *
     * @param lfPayData
     * @return
     */
    private long calculateTotalAmount(LfPayData lfPayData) {
        return lfPayData.remainAmount();
    }

    /**
     * PgData??? ??????????????? ???????????? CancelData??? ??????
     *
     * @param pgPayData
     * @param paymentCancelRequest
     * @return
     */
    private CancelData convertPgPayDataToIamportCancelData(Payment orgData, LfPayData pgPayData, PaymentCancelRequest paymentCancelRequest) {
        String imp_uid = pgPayData.impUid();
        long checksum = calculateTotalAmount(pgPayData);

        // CancelData ?????? ?????? ??????
        BankCode bankCode = getBankCode(orgData.pg_provider(), paymentCancelRequest.cancelRequest().refundBankCode());
        String cancelBankCode = setCancelRequestBankCode(orgData.pg_provider(), bankCode);

        if(orgData.pay_method().equals(PayType.findIamPortName("VBANK"))) {
            return new CancelData(imp_uid,
                    paymentCancelRequest.cancelRequest().orderId(),
                    paymentCancelRequest.cancelRequest().cancelRequestAmount(),
                    paymentCancelRequest.cancelRequest().cancelTaxFree() <= 0 ? 0 : paymentCancelRequest.cancelRequest().cancelTaxFree(),
                    paymentCancelRequest.cancelRequest().cancelVatAmount() <= 0 ? 0 : paymentCancelRequest.cancelRequest().cancelVatAmount(),
                    checksum,
                    paymentCancelRequest.cancelRequest().reason(),
                    paymentCancelRequest.cancelRequest().refundHolder(),
                    cancelBankCode,
                    paymentCancelRequest.cancelRequest().refundAccount(),
                    null
            );
        } else {
            return new CancelData(imp_uid,
                    paymentCancelRequest.cancelRequest().orderId(),
                    paymentCancelRequest.cancelRequest().cancelRequestAmount(),
                    paymentCancelRequest.cancelRequest().cancelTaxFree() <= 0 ? 0 : paymentCancelRequest.cancelRequest().cancelTaxFree(),
                    paymentCancelRequest.cancelRequest().cancelVatAmount() <= 0 ? 0 : paymentCancelRequest.cancelRequest().cancelVatAmount(),
                    checksum,
                    paymentCancelRequest.cancelRequest().reason(),
                    null,
                    null,
                    null,
                    null
            );
        }
    }

    /**
     * ???????????? ?????? API ??????
     *
     * @param cancelData
     * @return
     */
    private Payment cancel(CancelData cancelData) {
        AccessToken accessToken = iamportApi.getToken();
        Payment payment = iamportApi.cancel(accessToken.access_token(), cancelData);

        return payment;
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

    /**
     * BankCode ?????? ?????? ??????
     * @param pg
     * @param lfCode
     * @return
     */
    public BankCode getBankCode(String pg, String lfCode) {
        List<BankCode> bankCodeList = StreamSupport.stream(bankCodeRepository.findAll().spliterator(), false).collect(Collectors.toList());
        List<BankCode> filteredList;

        switch (pg) {
            case "settle":
                filteredList = bankCodeList.stream().filter(data -> data.settleBank() != null).collect(Collectors.toList());
                break;
            case "danal":
                filteredList = bankCodeList.stream().filter(data -> data.danal() != null).collect(Collectors.toList());
                break;
            default:
                filteredList = bankCodeList.stream().filter(data -> data.kgInicis() != null).collect(Collectors.toList());
                break;
        }

        return filteredList.stream().filter(data -> data.lfCode().equals(lfCode)).findFirst().orElseThrow(() -> new LFException(ErrorMessages.CODE_3507));
    }

    /**
     * ??????????????? ???????????? ?????? ?????? ??????
     * @param pg
     * @param bankCode
     * @return
     */
    public String setCancelRequestBankCode(String pg, BankCode bankCode) {
        String result = switch (pg) {
            case "settle":
                yield  bankCode.settleBank();
            case "danal":
                yield bankCode.danal();
            default :
                yield bankCode.kgInicis();
        };

        return result;
    }
}