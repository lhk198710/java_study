package com.example.study.api.payment.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(schema = "payment", name = "pg_pay_methods")
public record PgPayMethod(@Id Long pgPayMethodsId,
                          String payMethod,
                          String payMethodName,
                          int inicis,
                          int kcp,
                          int danal,
                          int minAmount,
                          int maxAmount) {
}
