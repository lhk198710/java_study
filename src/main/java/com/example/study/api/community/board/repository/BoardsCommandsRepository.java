package com.example.study.api.community.board.repository;

import com.example.study.api.community.board.BoardEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface BoardsCommandsRepository extends CrudRepository<BoardEntity, Long> {

    @Modifying
    @Query("update community.boards set board_code = :boardCode, `name` = :name, `status` = :status where board_id = :boardId")
    boolean update(
            @Param("boardId") long boardId,
            @Param("boardCode") String boardCode,
            @Param("name") String name,
            @Param("status") int status);

    @Modifying
    @Query("update community.boards set `status` = :status where board_id = :boardId")
    boolean updateStatus(
            @Param("boardId") long boardId,
            @Param("status") int status);
}
