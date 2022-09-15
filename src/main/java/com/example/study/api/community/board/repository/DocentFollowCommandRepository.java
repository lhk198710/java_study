package com.example.study.api.community.board.repository;

import com.example.study.api.community.board.DocentFollowerEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface DocentFollowCommandRepository extends CrudRepository<DocentFollowerEntity, Long> {
    @Modifying
    @Query("delete from docent.docent_followers where docent_follow_id = :docentFollowId ")
    boolean docentFollowDelete(@Param("docentFollowId") long docentFollowId);
}
