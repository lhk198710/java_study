package com.example.study.api.payment.service;

import com.example.study.api.payment.common.constant.PgType;
import com.example.study.api.payment.entity.PgPayMethod;
import com.example.study.api.payment.model.response.PaymentPgMethodViewResponse;
import com.example.study.api.payment.repository.PgPayMethodRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Service
public class PaymentViewService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentViewService.class);

    private PgPayMethodRepository pgPayMethodRepository;

    public PaymentViewService(PgPayMethodRepository pgPayMethodRepository) {
        this.pgPayMethodRepository = pgPayMethodRepository;
    }

    public Set<PaymentPgMethodViewResponse> getPaymentPgViewResponse() {
        Set<PaymentPgMethodViewResponse> paymentMethodViewRespons = new HashSet<PaymentPgMethodViewResponse>();
        Iterable<PgPayMethod> pgPayMethodLists = pgPayMethodRepository.findAll();

        pgPayMethodLists.forEach(pgPayMethod -> {
            PaymentPgMethodViewResponse paymentViewResponse = setPgRate(pgPayMethod);
            paymentMethodViewRespons.add(paymentViewResponse);
        });

        return paymentMethodViewRespons;
    }

    public PaymentPgMethodViewResponse setPgRate(PgPayMethod paPayMethodList) {
        ArrayList<HashMap<String, String>> pgRateList = new ArrayList<HashMap<String,String>>();

        // 이니시스 비율 설정
        pgRateList.add(new HashMap<String, String>(){
            {
                put("pg", PgType.getIamPortPgName(PgType.INICIS.name()));
                put("rate", String.valueOf(paPayMethodList.inicis()));
            }
        });

        // 다날 비율 설정
        pgRateList.add(new HashMap<String, String>(){
            {
                put("pg", PgType.getIamPortPgName(PgType.DANAL.name()));
                put("rate", String.valueOf(paPayMethodList.danal()));
            }
        });

        // KCP 비율 설정
        pgRateList.add(new HashMap<String, String>(){
            {
                put("pg", PgType.getIamPortPgName(PgType.KCP.name()));
                put("rate", String.valueOf(paPayMethodList.kcp()));
            }
        });

        String selectedPg = this.calculatePgRate(pgRateList);

        return new PaymentPgMethodViewResponse(paPayMethodList.payMethod(), paPayMethodList.payMethodName(), selectedPg);
    }

    public String calculatePgRate(ArrayList<HashMap<String, String>> pgRateList) {
        String result = "";

        double tmpRandom = (Math.random() * 100);
        double tmpRatePrev = 0, tmpRateNext = 0;

        for(int i = 0; i < pgRateList.size(); i++) {
            if(tmpRandom == 100) {
                result = pgRateList.get(pgRateList.size() - 1).get("pg");
                break;
            } else {
                double rate = Double.parseDouble(pgRateList.get(i).get("rate"));
                tmpRateNext = tmpRatePrev + rate;
                if(tmpRandom >= tmpRatePrev && tmpRandom < tmpRateNext) {
                    result = pgRateList.get(i).get("pg");
                    break;
                } else {
                    tmpRatePrev = tmpRateNext;
                }
            }
        }

        return result;
    }
}
