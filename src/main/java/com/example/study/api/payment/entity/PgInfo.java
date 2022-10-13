package com.example.study.api.payment.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(schema = "payment", name = "pg_infos")
public record PgInfo(@Id Long pgId,
                     String pgKey,
                     String lfKey,
                     String pgName,
                     int available,
                     int defaultPg) {
}
