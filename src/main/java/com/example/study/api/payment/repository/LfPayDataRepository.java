package com.example.study.api.payment.repository;

import com.example.study.api.payment.entity.LfPayData;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LfPayDataRepository extends CrudRepository<LfPayData, Long> {
    /**
     * 주문 번호 기준 전체 거래 데이터 조회
     * @param orderId
     * @return
     */
    List<LfPayData> findAllByOrderID(String orderId);

    @Modifying
    @Query("UPDATE lf_pay_data SET status = :afterStatus" + " WHERE order_id = :orderId AND imp_uid = :imp_uid AND status = :beforeStatus")
    int updateStatus(String beforeStatus, String orderId, String imp_uid, String afterStatus);

    /**
     * 주문 번호 기준 마지막 거래 데이터 조회
     * @param orderId
     * @return
     */
    @Transactional(readOnly = true)
    LfPayData findTopByOrderIDOrderByLfPayDataIdDesc(String orderId);
}
