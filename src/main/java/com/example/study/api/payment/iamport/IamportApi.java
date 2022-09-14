package com.example.study.api.payment.iamport;

import com.example.study.api.payment.common.exception.LFException;
import com.example.study.api.payment.iamport.model.*;
import com.example.study.api.payment.common.exception.IamportResponseException;
import com.example.study.api.payment.service.WebClients;
import io.netty.channel.ConnectTimeoutException;
import io.netty.handler.timeout.ReadTimeoutException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.http.HttpTimeoutException;

@Component
public class IamportApi {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(IamportApi.class);

    private static ParameterizedTypeReference<IamportResponse<AccessToken>> ACCESS_TOKEN = new ParameterizedTypeReference<IamportResponse<AccessToken>>() {};
    private static ParameterizedTypeReference<IamportResponse<Payment>> PAYMENT = new ParameterizedTypeReference<IamportResponse<Payment>>() {};
    private static ParameterizedTypeReference<IamportResponse<Payment>> CANCEL = new ParameterizedTypeReference<IamportResponse<Payment>>() {};
    private static ParameterizedTypeReference<IamportResponse<PrepareData>> PREPARE = new ParameterizedTypeReference<IamportResponse<PrepareData>>() {};

    private final WebClient webClient;

    @Value("${iamport.data.id}")
    private String id;

    @Value("${iamport.data.key}")
    private String key;

    @Value("${iamport.data.secret}")
    private String secret;

    @Value("${iamport.data.connectTimeout}")
    private int connectTimeout;

    @Value("${iamport.data.readTimeout}")
    private int readTimeout;

    @Value("${iamport.data.retryCnt}")
    private int retryCnt;

    public IamportApi(@Value("${iamport.data.host}") String host) {
        this.webClient = WebClients.client(host, connectTimeout, readTimeout);
    }

    /**
     * 아임포트 token 획득
     * @return
     */
    public AccessToken getToken() {
        AuthData authData = new AuthData(key, secret);
        IamportResponse<AccessToken> results = webClient.post().uri("/users/getToken")
                .bodyValue(authData)
                .retrieve()
                .onStatus(
                        httpStatus -> httpStatus != HttpStatus.OK, clientResponse -> {
                            return setIamportResponseException(clientResponse);
                        }
                )
                .bodyToMono(ACCESS_TOKEN)
                .block();

        LOGGER.info("uri : {}, req : {}, res: {}", "/users/getToken", authData, results);

        return this.response(results);
    }

    /**
     * imp_uid 기준 아임포트 서버에서 결제 정보 조회
     */
    public Payment getPaymentData(String access_token, String imp_uid) {
        IamportResponse<Payment> results = webClient.get().uri(uriBuilder -> uriBuilder.path("/payments/{imp_uid}").build(imp_uid))
                .header("Authorization", access_token)
                .retrieve()
                .onStatus(
                        httpStatus -> httpStatus != HttpStatus.OK, clientResponse -> {
                            return setIamportResponseException(clientResponse);
                        }
                )
                .bodyToMono(PAYMENT)
                .block();

        LOGGER.info("uri : {}, imp_uid : {}, res: {}", "/payments/" + imp_uid, imp_uid, results);

        return this.response(results);
    }

    /**
     * 아임포트에 환불 요청
     *  imp_uid, merchant_uid 모두 인입 시 : imp_uid 기준 취소
     *  merchant_uid만 인입 시 : merchant_uid 기준 취소
     */
    public Payment cancel(String access_token, CancelData cancelData) {
        IamportResponse<Payment> results = webClient.post().uri("/payments/cancel")
                .header("Authorization", access_token)
                .bodyValue(cancelData)
                .retrieve()
                .onStatus(
                        httpStatus -> httpStatus != HttpStatus.OK, clientResponse -> {
                            return setIamportResponseException(clientResponse);
                        }
                )
                .bodyToMono(CANCEL)
                .block();

        LOGGER.info("uri : {}, req : {}, res: {}", "/payments/cancel", cancelData, results);

        return this.response(results);
    }

    public PrepareData setPrepare(String access_token, PrepareData prepareData) {
        IamportResponse<PrepareData> results = webClient.post().uri("/payments/prepare")
                .header("Authorization", access_token)
                .bodyValue(prepareData)
                .retrieve()
                .onStatus(
                        httpStatus -> httpStatus != HttpStatus.OK, clientResponse -> {
                            return setIamportResponseException(clientResponse);
                        }
                )
                .bodyToMono(PREPARE)
                .block();

        LOGGER.info("uri : {}, req : {}, res: {}", "/payments/prepare", prepareData, results);

        return this.response(results);
    }

    public PrepareData getPrepare(String access_token, String merchant_uid) {
        IamportResponse<PrepareData> results = webClient.get().uri("/payments/prepare/" + merchant_uid)
                .header("Authorization", access_token)
                .retrieve()
                .onStatus(
                        httpStatus -> httpStatus != HttpStatus.OK, clientResponse -> {
                            return setIamportResponseException(clientResponse);
                        }
                )
                .bodyToMono(PREPARE)
                .block();

        LOGGER.info("uri : {}, merchant_uid : {}, res: {}", "/payments/prepare", merchant_uid, results);

        return this.response(results);
    }

    private <T> T response(IamportResponse<T> results) {
        if(results.code() == 0) {
            return results.response();
        } else {
            LOGGER.error("code : {}, message : {}, response : {}", results.code(), results.message(), results.response());
            throw new IllegalStateException(results.message());
        }
    }

    private Mono<? extends Throwable> setIamportResponseException(ClientResponse clientResponse) {
        if(clientResponse.statusCode().is4xxClientError()) {
            if (clientResponse.statusCode().equals(HttpStatus.UNAUTHORIZED)) {
                return clientResponse.createException().flatMap(it -> Mono.error(new LFException("인증 실패 거래건입니다.")));
            } else {
                return clientResponse.createException().flatMap(it -> Mono.error(new LFException("존재하지 않는 거래건입니다.")));
            }

        } else if(clientResponse.statusCode().is5xxServerError()) {
            return clientResponse.createException().flatMap(it -> Mono.error(new LFException("내부 시스템 오류입니다. [아임포트]")));
        } else {
            return clientResponse.createException().flatMap(it -> Mono.error(new LFException("내부 시스템 오류입니다. [LF]")));
        }
    }
}
