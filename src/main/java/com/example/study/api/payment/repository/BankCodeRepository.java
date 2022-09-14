package com.example.study.api.payment.repository;

import com.example.study.api.payment.entity.BankCode;
import org.springframework.data.repository.CrudRepository;

public interface BankCodeRepository extends CrudRepository<BankCode, Long> {
    // 각 은행사별 은행 코드 추출 필요
    // 각 PG별 은행 코드 추출 필요

}
