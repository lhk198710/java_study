package com.example.study.api.payment.common.constant;

public enum PayType {
    /** 신용카드 */
    CARD("신용카드", "card", "CARD"),
    /** 가상계좌 */
    VBANK("가상계좌", "vbank", "VBANK"),
    /** HP(휴대폰) */
    HP("휴대폰결제", "phone", "HP");

    String comment;
    String iamPortName;
    String lfName;

    PayType(String comment, String iamPortName, String lfName) {
        this.comment = comment;
        this.iamPortName = iamPortName;
        this.lfName = lfName;
    }

    public static String findIamPortName(String status) {
        for (PayType sType : values()) {
            if (status.equals(sType.name())) {
                return sType.iamPortName;
            }
        }
        return null;
    }
}
