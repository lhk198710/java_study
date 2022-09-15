package com.example.study.api.community.board.repository;

import com.example.study.api.community.board.DocentLikeEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface DocentLikeCommandRepository extends CrudRepository<DocentLikeEntity, Long> {
    @Modifying
    @Query("delete from docent.docent_likes where docent_like_id = :docentLikeId ")
    boolean deleteDocentLike(@Param("docentLikeId") long docentLikeId);
}
