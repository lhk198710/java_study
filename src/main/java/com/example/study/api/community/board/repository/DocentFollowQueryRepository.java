package com.example.study.api.community.board.repository;

import com.example.study.api.community.board.DocentFollowerEntity;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface DocentFollowQueryRepository extends Repository<DocentFollowerEntity, Long> {
    DocentFollowerEntity findByDocentIdAndMemberId(long docentId, long memberId);
}
