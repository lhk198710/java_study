package com.example.study.api.payment.model.request;

public record NotificationRequest(String imp_uid,
                                  String merchant_uid,
                                  String status) {
}
