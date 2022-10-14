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
        // 내부에 저장된 결제 데이터 조회
        LfPayData lastLfPayData = this.getLastLfPayData(orderId);

        // 아임포트 백엔드를 통해 결제 데이터 조회
        Payment orgData = getPaymentData(lastLfPayData.impUid());

        // Req로 들어온 데이터와 내부에 저장된 데이터 비교
        validateIamportBackendAndWebData(lastLfPayData, paymentCancelRequest);

        StatusType statusType = setCancelStatus(orgData, paymentCancelRequest, lastLfPayData);

        // 아임포트 취소 가능여부 확인
        boolean checkIamportCancelable = checkIamportCancelable(orgData, paymentCancelRequest, lastLfPayData);

        if(checkIamportCancelable) {
            // 아임포트 취소 요청 데이터 생성
            CancelData cancelData = this.convertPgPayDataToIamportCancelData(orgData, lastLfPayData, paymentCancelRequest);

            try {
                // 아임포트 취소 요청
                Payment payment = this.cancel(cancelData);

                // 데이터 저장(아임포트 취소 성공 시)
                PgPayData insertData = PgPayData.ofNew(payment, LocalDateTime.now(), LocalDateTime.now());

                this.insertPgPayData(insertData);
                this.insertLfPayDataWithPayment(paymentCancelRequest, payment, statusType);
            } catch (LFException le) {
                le.getMessage();
                throw le;
                // 아임포트 API 취소 실패  : 현재는 아임포트 메시지를 그대로 응답 메시지로 노출시킴.
                // 아임포트 에러 메시지 포맷 예시 : code : 1, message : 취소가능금액 검증에 실패하였습니다. (취소가능금액(KRW) :  1,300.00 vs 900.00), response : null
                // DB에 취소 실패 저장 안 함
            } catch (Exception ex) {
                ex.getMessage();
                throw ex;
                // 기타 실패
                // 취소는 성공했으나 DB 저장에 실패 등.
                // 이 부분은 내용 보충 필요
            }

        } else {
            // 데이터 저장
            this.insertLfPayDataWithoutPayment(paymentCancelRequest, lastLfPayData, statusType);
        }

        // 응답 데이터 생성
        PaymentCancelResponse paymentCancelResponse = this.makePaymentCancelResponse(true, "취소 성공", paymentCancelRequest);

        return paymentCancelResponse;
    }

    /**
     * 휴대폰 결제 익월 취소 가능 여부 판단을 위한 Period 객체 계산
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
     * 아임포트 API 사용 가능 상태 확인
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
            // 한번이라도 부분 취소가 된 건은 아임포트에 취소 시도하면 안된다.
            if(lastLfPayData.status().equals("PC")) {
                result = false;
            }

            // 휴대폰 부분 취소 시도
            if(cancelAmount != 0) {
                result = false;
            }

            // 휴대폰 익월 취소 시도
            if ((period.getMonths() > 0)) {
                result = false;
            }
        }

        // 가상계좌 입금 대기 시 취소
        if(payment.pay_method().equals(PayType.findIamPortName("VBANK")) && lastLfPayData.status().equals(StatusType.WD.name())) {
            result = false;
        }

        // 마지막 거래가 환불, 또는 부분 환불인 건 확인
        if(lastLfPayData.status().equals(StatusType.RF.name()) || lastLfPayData.status().equals(StatusType.PR.name())) {
            result = false;
        }

        return result;
    }

    /**
     * 취소 가능 금액확인
     * @param payment
     * @param paymentCancelRequest
     * @return
     */
    public long calculateCanceAmount(Payment payment, PaymentCancelRequest paymentCancelRequest) {
        return payment.amount() - payment.cancel_amount() - paymentCancelRequest.cancelRequest().cancelRequestAmount();
    }

    /**
     * Status 생성
     * @param payment
     * @param paymentCancelRequest
     * @param lastLfPayData
     * @return
     */
    public StatusType setCancelStatus(Payment payment, PaymentCancelRequest paymentCancelRequest, LfPayData lastLfPayData) {
        StatusType status = StatusType.CN;

        Period period = getPeriod(payment);
        long cancelAmount = calculateCanceAmount(payment, paymentCancelRequest);

        // 부분 취소 여부 우선 판단
        if(payment.amount() - payment.cancel_amount() > paymentCancelRequest.cancelRequest().cancelRequestAmount()) {
            status = StatusType.PC;
        }

        if (payment.pay_method().equals("phone")) {
            if ((period.getMonths() > 0)) {
                // 1. 익월 취소 시도
                if(cancelAmount == 0) {
                    status = StatusType.RF;
                } else {
                    status = StatusType.PR;
                }
            } else {
                // 2. 일반 취소 시도
                if(cancelAmount == 0) {
                    status = StatusType.CN;
                } else {
                    status = StatusType.PR;
                }
            }
        }

        if(payment.pay_method().equals(PayType.findIamPortName("VBANK")) && lastLfPayData.status().equals(StatusType.WD.name())) {
            if(cancelAmount == 0) {
                // 전체 취소
                status = StatusType.VN;
            } else {
                // 부분 취소
                // throw new LFException(ErrorMessages.CODE_3512);

                if(lastLfPayData.status().equals("WD")) {
                    status = StatusType.CN;
                } else {
                    // 부분 취소(StatusType.PC)로 입력할지 부분 환불(StatusType.PR)로 입력할지 아임포트 테스트 결과에 따라 달라짐
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
     * DB에 저장된 LF 원거래 건 조회
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
     * 취소 성공 후 응답 값 생성
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
     * 아임포트 취소 데이터 DB 저장
     *
     * @param pgPayData
     */
    @Transactional
    public void insertPgPayData(PgPayData pgPayData) {
        pgPayDataRepository.save(pgPayData);
    }

    /**
     * lf_pay_data DB 저장
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
     * 기본 데이터 체크
     *
     * @param lfPayData
     * @param paymentCancelRequest
     */
    private void validateIamportBackendAndWebData(LfPayData lfPayData, PaymentCancelRequest paymentCancelRequest) {
        // 1. 주문번호 기준 주문 내역 확인
        if (lfPayData == null || lfPayData.orderID() == null ||  lfPayData.orderID() == "") {
            throw new LFException(ErrorMessages.CODE_3601);
        }

        // 2. 금액 체크
        long orderedTotalAmount = calculateTotalAmount(lfPayData);  // 주문번호 기준 잔액
        long cancelRequestAmount = paymentCancelRequest.cancelRequest().cancelRequestAmount(); // 클라이언트를 통해 전달 받은 취소 금액

        LOGGER.info("orderID : {}, orderedTotalAmount : {}, cancelRequestAmount : {}", lfPayData.orderID(), orderedTotalAmount, cancelRequestAmount);

        if (orderedTotalAmount == 0L) {
            throw new LFException(ErrorMessages.CODE_3605);
        }

        if (orderedTotalAmount < cancelRequestAmount) {
            throw new LFException(ErrorMessages.CODE_3503, String.valueOf(orderedTotalAmount));
        }

        // 4. 결제수단 기준 익월 취소 가능 여부 확인
        LocalDate paidAtTime = LocalDate.ofInstant(Instant.ofEpochSecond(Long.parseLong(lfPayData.paidAt())), TimeZone.getDefault().toZoneId());
        LocalDate nowTime = LocalDate.now();

        Period period = Period.between(paidAtTime.withDayOfMonth(1), nowTime.withDayOfMonth(1));
        if ((period.getMonths() > 0) && lfPayData.payMethod().equals("phone")) {
            throw new LFException(ErrorMessages.CODE_3519);
        }
    }

    /**
     * 주문번호 기준 잔액 계산시 사용
     *
     * @param lfPayData
     * @return
     */
    private long calculateTotalAmount(LfPayData lfPayData) {
        return lfPayData.remainAmount();
    }

    /**
     * PgData를 아임포트에 전달하는 CancelData로 전환
     *
     * @param pgPayData
     * @param paymentCancelRequest
     * @return
     */
    private CancelData convertPgPayDataToIamportCancelData(Payment orgData, LfPayData pgPayData, PaymentCancelRequest paymentCancelRequest) {
        String imp_uid = pgPayData.impUid();
        long checksum = calculateTotalAmount(pgPayData);

        // CancelData 은행 코드 설정
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
     * 아임포트 취소 API 호출
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
     * 아임포트 결제 데이터 획득 API 호출
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
     * BankCode 열거 객체 생성
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
     * 아임포트에 전달하는 은행 코드 생성
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