package com.example.study.api.payment.common.exception;

public enum PayMessage {
    INTERNAL_EXCEPTION("0001", "시스템 에러입니다."),
    INVALID_PARAMETER("0002", "부적절한 PARAMETER가 들어왔습니다.");

    public String code;
    public String message;

    PayMessage(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
