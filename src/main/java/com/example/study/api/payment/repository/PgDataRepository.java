package com.example.study.api.payment.repository;

import com.example.study.api.payment.entity.PgData;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PgDataRepository extends CrudRepository<PgData, Long> {
    Optional<PgData> findByMerchantUid(String merchant_uid);
    List<PgData> findAllByMerchantUid(String merchant_uid);
}
