package com.example.study.api.payment.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(schema = "payment", name = "bank_codes")
public record BankCode(@Id long bankCodeId,
                       String bankName,
                       String lfCode,
                       String settleBank,
                       String kgInicis,
                       String nhnKcp,
                       String lgUplus,
                       String nicePayments,
                       String jtnet,
                       String danal,
                       String kicc,
                       String smartro,
                       String daouData) {
}
