package com.example.study.api.payment.service;

import com.example.study.api.payment.common.constant.PgType;
import com.example.study.api.payment.common.exception.ErrorMessages;
import com.example.study.api.payment.common.exception.LFException;
import com.example.study.api.payment.common.exception.RollbackException;
import com.example.study.api.payment.entity.PgInfo;
import com.example.study.api.payment.entity.PgPayData;
import com.example.study.api.payment.entity.PgPayMethod;
import com.example.study.api.payment.iamport.IamportApi;
import com.example.study.api.payment.iamport.model.PrepareData;
import com.example.study.api.payment.model.request.IamportPayRequest;
import com.example.study.api.payment.model.response.IamportPayResponse;
import com.example.study.api.payment.model.response.PaymentMethodViewResponse;
import com.example.study.api.payment.model.response.PaymentPgMethodViewResponse;
import com.example.study.api.payment.model.response.PaymentResponse;
import com.example.study.api.payment.common.constant.IamportStatusConstant;
import com.example.study.api.payment.entity.LfPayData;
import com.example.study.api.payment.iamport.model.AccessToken;
import com.example.study.api.payment.iamport.model.Payment;
import com.example.study.api.payment.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class PaymentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentService.class);

    private PgPayMethodRepository pgPayMethodRepository;
    private PaymentViewService paymentViewService;
    private IamportApi iamportApi;
    private PgPayDataRepository pgPayDataRepository;
    private LfPayDataRepository lfPayDataRepository;
    private PgInfoRepository pgInfoRepository;
    private BankCodeRepository bankCodeRepository;

    public PaymentService(PgPayMethodRepository pgPayMethodRepository, PaymentViewService paymentViewService, IamportApi iamportApi, PgPayDataRepository pgPayDataRepository, LfPayDataRepository lfPayDataRepository, PgInfoRepository pgInfoRepository, BankCodeRepository bankCodeRepository) {
        this.pgPayMethodRepository = pgPayMethodRepository;
        this.paymentViewService = paymentViewService;
        this.iamportApi = iamportApi;
        this.pgPayDataRepository = pgPayDataRepository;
        this.lfPayDataRepository = lfPayDataRepository;
        this.pgInfoRepository = pgInfoRepository;
        this.bankCodeRepository = bankCodeRepository;
    }

    /**
     * 결제 수단, PG 데이터 응답
     *
     * @return
     */
    public PaymentMethodViewResponse getPaymentMethodViewResponse() {
        PaymentMethodViewResponse paymentMethodViewResponse = null;

        try {
            // 1. 사용가능한 결제 수단, 결제 수단 별 PG사 설정
            Set<PaymentPgMethodViewResponse> paymentMethodViewRespons = paymentViewService.getPaymentPgViewResponse();

            // 2. 주문 창 내 결제 수단 응답 데이터 설정
            paymentMethodViewResponse = new PaymentMethodViewResponse(paymentMethodViewRespons);

        } catch (Exception e) {
            throw new LFException(e, "");
        }

        return paymentMethodViewResponse;
    }

    public IamportPayResponse getPaymentAuthViewResponse(String orderId, IamportPayRequest iamportPayRequest) {
        // 1. 전달 받은 pg사가 유효한 PG사인지 확인
        checkPgInfo(iamportPayRequest.pg());

        // 2. 전달 받은 pay_method가 유효한 pay_method인지 확인
        PgPayMethod pgPayMethod = checkPayMethod(iamportPayRequest.pay_method(), iamportPayRequest.pg());

        // 3. amount 값 체크
        checkAmount(iamportPayRequest.amount(), pgPayMethod);

        // 4. 주문 번호 기준 기존 거래 완료건인지 조회
        checkDuplicatedOrder(orderId);

        // 4. 주문번호, amount 유효성 검증(검증 방법 고민 필요)

        // 5. 아임포트 Prepare 실행 :: 사용자가 중간에 껏다가 다시 실행하면 아임포트에 미리 저장이 되어 있어 에러가 발생함
        // setIamportPrepare(orderId, iamportPayRequest);

        // 6. 응답 데이터 생성
        return makeIamportPayResponse(orderId, iamportPayRequest);
    }

    /**
     * 아임포트에 결제 데이터 미리 저장(검증 목적)
     * @param orderId
     * @param iamportPayRequest
     */
    private void setIamportPrepare(String orderId, IamportPayRequest iamportPayRequest) {
        AccessToken accessToken = iamportApi.getToken();
        PrepareData prepareData = new PrepareData(orderId, iamportPayRequest.amount());
        iamportApi.setPrepare(accessToken.access_token(), prepareData);
    }

    /**
     * 아임포트 결제창 호출 데이터 응답
     * @param orderId
     * @param iamportPayRequest
     * @return
     */
    private IamportPayResponse makeIamportPayResponse(String orderId, IamportPayRequest iamportPayRequest) {
        return IamportPayResponse.makeIamportPayResponse(iamportPayRequest.pg(), iamportPayRequest.pay_method(), iamportPayRequest.amount(), orderId, iamportPayRequest.name());
    }

    /**
     * 최소 금액 체크
     *  - 결제수단별 체크 필요
     * @param amount
     */
    private void checkAmount(long amount, PgPayMethod pgPayMethod) {
        int minAmount = pgPayMethod.minAmount();
        int maxAmount = pgPayMethod.maxAmount();
        if(minAmount > amount || maxAmount < amount) {
            throw new LFException(ErrorMessages.CODE_3604);
        }
    }

    /**
     * 전달 받은 pg사가 유효한 PG사인지 확인
     * @param pg
     */
    private void checkPgInfo(String pg) {
        PgInfo pgInfo = pgInfoRepository.findTopByPgKeyAndAvailable(pg, 1);

        if(pgInfo == null) {
            throw new LFException(ErrorMessages.CODE_3514);
        }
    }

    /**
     * 전달 받은 pay_method가 유효한 pay_method인지 확인
     * @param payMethod
     * @param pg
     */
    private PgPayMethod checkPayMethod(String payMethod, String pg) {
        int rate = 0;
        PgPayMethod pgPayMethod = pgPayMethodRepository.findTopByPayMethod(payMethod);

        if(pgPayMethod == null) {
            throw new LFException(ErrorMessages.CODE_3511);
        }

        PgType pgType = PgType.findByIamPortPgName(pg);

        switch (pgType.name()) {
            case "INICIS":
                rate = pgPayMethod.inicis();
                break;
            case "KCP":
                rate = pgPayMethod.kcp();
                break;
            case "DANAL":
                rate = pgPayMethod.danal();
                break;
            default:
                break;
        }

        if(rate == 0) {
            throw new LFException(ErrorMessages.CODE_3511);
        }

        return pgPayMethod;
    }

    /**
     * PG사 결제 완료 데이터 저장
     *
     * @param orderId
     * @param imp_uid
     * @param merchant_uid
     * @param imp_success
     * @return
     */
    public PaymentResponse setPayOrder(String orderId, String imp_uid, String merchant_uid, boolean imp_success) {
        // 1. 내부 주문번호, 아임포트 주문번호 기준 중복 거래 조회(DB에 입력된 거래라면 중복 거래로 판단)
        checkDuplicatedOrder(orderId);

        // 2. 아임포트 데이터 조회
        Payment payment = getPaymentData(imp_uid);
        // PrepareData prepareData = getPrepare(merchant_uid);

        // 3. 주문 번호, 금액 비교
        validateIamportBEAndWebData(payment, imp_uid, merchant_uid, null);

        // 4. 아임포트 결제 데이터 저장(로그성)
        insertPgPayData(PgPayData.ofNew(payment, LocalDateTime.now(), LocalDateTime.now()));

        // 5. LF 원장 데이터 저장
        LfPayData lfPayData = LfPayData.makeLfPayData(payment);
        this.insertLfPayData(lfPayData);

        // 6. 응답 데이터 생성
        PaymentResponse paymentResponse = makePaymentResponseNew(true, payment.status().equals("paid") || payment.status().equals("ready") ? "성공" : "실패", payment);

        return paymentResponse;
    }

    private void checkDuplicatedOrder(String orderId) {
        LfPayData lfPayData = lfPayDataRepository.findTopByOrderIDOrderByLfPayDataIdDesc(orderId);
        PgPayData pgPayData = pgPayDataRepository.findTopByMerchantUidOrderByPgPayDataIDDesc(orderId);

        if(lfPayData != null && pgPayData != null) {
            throw new LFException(ErrorMessages.CODE_3606);
        }
    }

    /**
     * 아임포트 Prepare 데이터 조회
     * @param orderId
     * @return
     */
    private PrepareData getPrepare(String orderId) {
        AccessToken accessToken = iamportApi.getToken();
        PrepareData prepareData = iamportApi.getPrepare(accessToken.access_token(), orderId);

        return prepareData;
    }

    /**
     * 결제 데이터 저장 요청에 대한 응답값 생성
     *
     * @param success
     * @param message
     * @param payment
     * @return
     */
    private PaymentResponse makePaymentResponseNew(boolean success, String message, Payment payment) {
        return new PaymentResponse(success, message, payment.merchant_uid(), payment.amount(), payment.pay_method(), payment.pg_provider());
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
     * 아임포트 백엔드를 통해 전달 받은 결제 정보와 LF 결제 정보 비교가 필요함
     * *  - 2022.07.19 기준, LF 결제 정보의 경우 GET 으로 받은 가격, 아임포트 주문번호, LF 주문번호만 확인이 가능하니 우선 그것들만 비교 할 것
     *
     * @param payment
     * @param imp_uid
     * @param merchant_uid
     */
    private void validateIamportBEAndWebData(Payment payment, String imp_uid, String merchant_uid, PrepareData prepareData) {
        if (!imp_uid.equals(payment.imp_uid())) {
            throw new LFException(ErrorMessages.CODE_3601);
        }

        if (!merchant_uid.equals(payment.merchant_uid())) {
            throw new LFException(ErrorMessages.CODE_3601);
        }

//        if(payment.amount() != prepareData.amount()) {
//            throw new IllegalArgumentException("유효하지 않은 금액 입니다.");
//        }
    }

    /**
     * LF 주문결제 내역 전체 조회(주문번호 기준)
     *
     * @param orderId
     * @pgPay
     */
    public List<LfPayData> getLfPayData(String orderId) {
        List<LfPayData> lfPayData = lfPayDataRepository.findAllByOrderID(orderId);

        if (lfPayData.size() == 0) {
            throw new LFException(ErrorMessages.CODE_3601);
        } else {
            return lfPayData;
        }
    }

    /**
     * LF 주문결제 내역 DB 저장
     *
     * @param lfPayData
     * @pgPay
     */
    @Transactional
    public LfPayData insertLfPayData(LfPayData lfPayData) {
        return lfPayDataRepository.save(lfPayData);
    }

    /**
     * 아임포트 결제데이터 DB 저장(로그성)
     *
     * @param pgPayData
     */
    @Transactional
    public PgPayData insertPgPayData(PgPayData pgPayData) {
        return pgPayDataRepository.save(pgPayData);
    }
}
