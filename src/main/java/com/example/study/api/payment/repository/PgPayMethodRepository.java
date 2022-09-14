package com.example.study.api.payment.repository;

import com.example.study.api.payment.entity.PgPayMethod;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface PgPayMethodRepository extends CrudRepository<PgPayMethod, Long> {
    @Transactional(readOnly = true)
    PgPayMethod findTopByPayMethod(String payMethod);
}
