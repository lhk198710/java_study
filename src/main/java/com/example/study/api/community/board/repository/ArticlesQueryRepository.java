package com.example.study.api.community.board.repository;

import com.example.study.api.community.board.BoardArticleEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface ArticlesQueryRepository extends Repository<BoardArticleEntity, Long> {

    String commonListQuery = "from community.board_articles where board_id = :boardId and board_article_id = ref_article_id and `status` = :status ";

    @Query("select count(1) " + commonListQuery)
    Long countAllByBoardIdAndStatus(
            @Param("boardId") long boardId,
            @Param("status") int status);

    @Query("select * " + commonListQuery + "order by completed asc " +
            "limit :offset, :size")
    List<BoardArticleEntity> findAllByBoardIdAndStatusOrderByCompletedAsc(
            @Param("boardId") long boardId,
            @Param("status") int status,
            @Param("offset") int offset,
            @Param("size") int size);

    @Query("select * " + commonListQuery + "order by `view` asc, completed asc " +
            "limit :offset, :size")
    List<BoardArticleEntity> findAllByBoardIdAndStatusOrderByViewAsc(
            @Param("boardId") long boardId,
            @Param("status") int status,
            @Param("offset") int offset,
            @Param("size") int size);

    @Query("select * " + commonListQuery + "order by `like` asc, completed asc " +
            "limit :offset, :size")
    List<BoardArticleEntity> findAllByBoardIdAndStatusOrderByLikeAsc(
            @Param("boardId") long boardId,
            @Param("status") int status,
            @Param("offset") int offset,
            @Param("size") int size);

    @Query("select * " + commonListQuery + "order by completed desc " +
            "limit :offset, :size")
    List<BoardArticleEntity> findAllByBoardIdAndStatusOrderByCompletedDesc(
            @Param("boardId") long boardId,
            @Param("status") int status,
            @Param("offset") int offset,
            @Param("size") int size);

    @Query("select * " + commonListQuery + "order by `view` desc, completed desc " +
            "limit :offset, :size")
    List<BoardArticleEntity> findAllByBoardIdAndStatusOrderByViewDesc(
            @Param("boardId") long boardId,
            @Param("status") int status,
            @Param("offset") int offset,
            @Param("size") int size);

    @Query("select * " + commonListQuery + "order by `like` desc, completed desc " +
            "limit :offset, :size")
    List<BoardArticleEntity> findAllByBoardIdAndStatusOrderByLikeDesc(
            @Param("boardId") long boardId,
            @Param("status") int status,
            @Param("offset") int offset,
            @Param("size") int size);

    BoardArticleEntity findByBoardArticleId(long articleId);
}
