package com.example.study.api.payment.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table(schema = "payment", name = "pg_data")
public record PgData(@Id Long pgDataId,
                     boolean success,
                     String status,
                     String errorCode,
                     String errorMsg,
                     String impUid,
                     String merchantUid,
                     String payMethod,
                     Long paidAmount,
                     String name,
                     String pgProvider,
                     String embPgProvider,
                     String pgTid,
                     String paidAt,
                     String receiptUrl,
                     String applyNum,
                     String vbankNum,
                     String vbankName,
                     String vbankHolder,
                     String vbankDate,
                     LocalDateTime createdAt,
                     LocalDateTime updatedAt) {
}
