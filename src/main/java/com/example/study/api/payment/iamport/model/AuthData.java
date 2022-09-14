package com.example.study.api.payment.iamport.model;

public record AuthData(String imp_key,
                       String imp_secret) {
    public AuthData(String imp_key, String imp_secret) {
        this.imp_key = imp_key;
        this.imp_secret = imp_secret;
    }
}
