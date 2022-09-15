package com.example.study.api.community.board.repository;

import com.example.study.api.community.board.DocentEntity;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface DocentQueryRepository extends Repository<DocentEntity, Long> {
    DocentEntity findByNickname(String nickname);
    DocentEntity findByMobile(String mobile);
    DocentEntity findByDocentId(long docentId);
}
