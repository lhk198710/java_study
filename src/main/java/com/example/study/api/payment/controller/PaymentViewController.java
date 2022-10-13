package com.example.study.api.payment.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/view/v1/payments")
public class PaymentViewController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentViewController.class);

    @RequestMapping(value = "/iamportTest", method= RequestMethod.GET)
    public String viewIamportTest(Model model, HttpServletRequest httpServletRequest) {
        String reqUri = httpServletRequest.getRequestURI();
        return "content/iamport_sample";
    }

    /**
     * 모바일 환경에서 일부 PG사 결제 진행 시 호출되는 페이지
     * @param model
     * @param httpServletRequest
     * @param imp_uid
     * @param merchant_uid
     * @param imp_success
     * @param error_msg
     * @return
     */
    @RequestMapping(value = "/iamportRedirect", method = RequestMethod.GET)
    public String viewIamportRedirect(Model model, HttpServletRequest httpServletRequest,
                                      @RequestParam(value="imp_uid") String imp_uid,
                                      @RequestParam(value="merchant_uid") String merchant_uid,
                                      @RequestParam(value="imp_success") boolean imp_success,
                                      @RequestParam(value="error_msg", required = false) String error_msg) {
        String reqUri = httpServletRequest.getRequestURI();
        System.err.println(error_msg);
        LOGGER.info("HttpServletRequest 데이터 : {}", httpServletRequest.getQueryString());
        return "content/iamport_redirect";
    }

    @RequestMapping(value = "/redirect", method = RequestMethod.GET)
    public String viewMobileRedirect(HttpServletRequest httpServletRequest,
                                     @RequestParam(value="imp_uid") String imp_uid,
                                     @RequestParam(value="merchant_uid") String merchant_uid,
                                     @RequestParam(value="imp_success") boolean imp_success,
                                     @RequestParam(value="error_msg", required = false) String error_msg) {
        String reqUri = httpServletRequest.getRequestURI();
        LOGGER.info("reqUri : {}, imp_uid : {}, merchant_uid : {}, imp_success : {}, error_msg : {}", reqUri, imp_uid, merchant_uid, imp_success, error_msg);
        return "content/iamport_redirect";
    }
}
