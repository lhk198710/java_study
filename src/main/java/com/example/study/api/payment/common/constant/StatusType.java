package com.example.study.api.payment.common.constant;

public enum StatusType {
    /** 대기 (wait) */
    WT,
    /** 완료/정상 (done) */
    DN,
    /** 가상계좌 입금 대기 (wait Depoits) */
    WD,
    /** 실패 (fail) */
    FL,
    /** av (available) */
    AV,
    /** 가상계좌 입금 전 취소 */
    VN,
    /** 취소 */
    CN,
    /** 부분취소 */
    PC,
    /** 환불 */
    RF,
    /** 부분환불 */
    PR;

    public static StatusType findBy(String status) {
        for (StatusType sType : values()) {
            if (status.equals(sType.name())) {
                return sType;
            }
        }
        return null;
    }
}
