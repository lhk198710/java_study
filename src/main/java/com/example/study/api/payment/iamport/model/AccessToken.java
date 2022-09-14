package com.example.study.api.payment.iamport.model;

public record AccessToken(String access_token,
                          int expired_at,
                          int now){

    public AccessToken(String access_token, int expired_at, int now) {
        this.access_token = access_token;
        this.expired_at = expired_at;
        this.now = now;
    }
}
