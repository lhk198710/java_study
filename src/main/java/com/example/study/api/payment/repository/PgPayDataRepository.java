package com.example.study.api.payment.repository;

import com.example.study.api.payment.entity.PgPayData;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PgPayDataRepository extends CrudRepository<PgPayData, Long> {
    @Transactional(readOnly = true)
    List<PgPayData> findAllByMerchantUid(String orderId);

    @Transactional(readOnly = true)
    PgPayData findTopByMerchantUidOrderByPgPayDataIDDesc(String orderId);

    @Modifying
    @Query("UPDATE pg_pay_data SET status = :afterStatus" + " WHERE merchant_uid = :orderId AND imp_uid = :imp_uid AND status = :beforeStatus")
    int updateStatus(String beforeStatus, String orderId, String imp_uid, String afterStatus);
}
