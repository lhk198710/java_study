package com.example.study.api.payment.repository;

import com.example.study.api.payment.entity.PgInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface PgInfoRepository extends CrudRepository<PgInfo, Long> {
    @Transactional(readOnly = true)
    PgInfo findTopByPgKey(String pgKey);

    @Transactional(readOnly = true)
    PgInfo findTopByPgKeyAndAvailable(String pgKey, int available);
}
