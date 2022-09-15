package com.example.study.api.community.board.repository;

import com.example.study.api.community.board.DocentLikeEntity;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface DocentLikeQueryRepository extends Repository<DocentLikeEntity, Long> {
    DocentLikeEntity findByDocentIdAndMemberId(long docentId, long memberId);
}
