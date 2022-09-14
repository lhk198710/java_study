package com.example.study.api.payment.common.constant;

public enum PgType {
    INICIS("이니시스 웹 표준", "inicis", "html5_inicis"),
    KCP("KCP", "kcp", "kcp"),
    DANAL("다날", "danal", "danal"),
    IAMPORT("아임포트", "iamport", "iamport");

    String pgName;
    String pgKey;
    String iamPortPgName;

    PgType(String pgName, String pgKey, String iamPortPgName) {
        this.pgName = pgName;
        this.pgKey = pgKey;
        this.iamPortPgName = iamPortPgName;
    }

    public static String getIamPortPgName(String pgName) {
        return valueOf(pgName).iamPortPgName;
    }

    public static PgType findByIamPortPgName(String status) {
        if(status == null) {
            return null;
        }

        for (PgType sType : values()) {
            if (status.equals(sType.iamPortPgName)) {
                return sType;
            }
        }
        return null;
    }
}
