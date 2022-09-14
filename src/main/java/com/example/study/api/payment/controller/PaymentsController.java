package com.example.study.api.payment.controller;

import com.example.study.api.result.Result;
import com.example.study.api.payment.entity.LfPayData;
import com.example.study.api.payment.model.request.IamportPayRequest;
import com.example.study.api.payment.model.request.NotificationRequest;
import com.example.study.api.payment.model.request.PaymentCancelRequest;
import com.example.study.api.payment.model.response.PaymentCancelResponse;
import com.example.study.api.payment.model.response.PaymentMethodViewResponse;
import com.example.study.api.payment.model.response.PaymentResponse;
import com.example.study.api.payment.service.NotiService;
import com.example.study.api.payment.service.PaymentCancelService;
import com.example.study.api.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Description;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentsController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentsController.class);
    private PaymentService paymentService;
    private PaymentCancelService paymentCancelService;
    private NotiService notiService;

    public PaymentsController(PaymentService paymentService, PaymentCancelService paymentCancelService, NotiService notiService) {
        this.paymentService = paymentService;
        this.paymentCancelService = paymentCancelService;
        this.notiService = notiService;
    }

    @GetMapping("/view")
    @Operation(summary="결제 수단 & 결제 수단 별 PG사 리스트 출력 API", description="결제 수단 & 결제 수단 별 PG사 리스트 출력 API.")
    public Result<PaymentMethodViewResponse> viewPayments() throws Exception {
        return Result.success(paymentService.getPaymentMethodViewResponse());
    }

    @ApiIgnore
    @PostMapping("/auth/{orderId}")
    @Operation(summary="결제 창 호출 시 필요 데이터 응답 API", description="결제 창 호출 시 필요 데이터 응답 API")
    public void authPayments(@PathVariable String orderId, @RequestBody IamportPayRequest iamportPayRequest) throws Exception {
        LOGGER.info("orderId : {}, iamportPayRequest : {}", orderId, iamportPayRequest);
        // 직접적인 주문 아이디 노출이 보안 상 우려가 있을 수도 있음.
        // 임시 아이디를 전달 받고 해당 아이디로 미리 저장 된 결제 정보(PG, 결제 수단, 금액, 할부 등 조회 필요)
        // 논의된 내용이 없으니 일단은 orderId로 명할 것
        // 주문 정보 체크 후 아임포트 데이터 내려줄 것
    }

    @GetMapping("/pay/{orderId}")
    @Operation(summary="PG사 결제 완료 후 PG 데이터 전달 API(Web)", description="PG사 결제 완료 후 PG 데이터 전달 API(Web)")
    public Result<PaymentResponse> payOrders(@PathVariable String orderId, @RequestParam(value="imp_uid") String imp_uid,
                                             @RequestParam(value="merchant_uid") String merchant_uid,
                                             @RequestParam(value="imp_success") boolean imp_success) throws Exception{
        LOGGER.info("orderId : {}, imp_uid : {}, merchant_uid : {}, imp_success : {}", orderId, imp_uid, merchant_uid, imp_success);
        return Result.success(paymentService.setPayOrder(orderId, imp_uid, merchant_uid, imp_success));
    }

    @PostMapping("/cancel/{orderId}")
    @Operation(summary="주문번호 기준 거래 취소", description="주문번호 기준 거래 취소")
    public Result<PaymentCancelResponse> cancelOrders(@PathVariable String orderId, @RequestBody PaymentCancelRequest paymentCancelRequest) throws Exception {
        LOGGER.info("orderId : {}, PaymentCancelRequest : {}", orderId, paymentCancelRequest);
        return Result.success(paymentCancelService.paymentCancel(orderId, paymentCancelRequest));
    }

    /**
     * 아임포트 to LF callBack API
     * @param notificationRequest
     * @return
     * @throws Exception
     */
    @PostMapping("/notification")
    @Operation(summary="아임포트 Webhook 수신", description="아임포트 Webhook 수신")
    public int callBack(@RequestBody NotificationRequest notificationRequest) throws Exception {
        LOGGER.info("NotificationRequest : {}", notificationRequest);
        return notiService.setNotiData(notificationRequest);
    }

    /**
     * 주문번호 기준 결제 데이터 조회
     * @param orderId
     * @return
     */
    @GetMapping("/search/{orderId}")
    @Operation(summary="주문번호 기준 결제 데이터 조회", description="주문번호 기준 결제 데이터 조회")
    public Result<List<LfPayData>> getOrders(@PathVariable String orderId) {
        return Result.success(paymentService.getLfPayData(orderId));
    }
}
