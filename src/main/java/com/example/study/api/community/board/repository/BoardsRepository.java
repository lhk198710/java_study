package com.example.study.api.community.board.repository;

import com.example.study.api.community.board.BoardEntity;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface BoardsRepository extends CrudRepository<BoardEntity, Long> {

    @Query("select * from community.boards where status = :status")
    List<BoardEntity> findAllByStatus(@Param("status") int status);

    BoardEntity findByBoardId(long boardId);
}
