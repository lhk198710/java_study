package com.example.study.api.community.board.repository;

import com.example.study.api.community.board.BoardArticleEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ArticlesCommandRepository extends CrudRepository<BoardArticleEntity, Long> {

    @Modifying
    @Query("update community.board_articles " +
            "set ref_article_id = :articleId, subject = :subject, contents = :contents, html = :html, completed = :completed, status = :status " +
            "where board_article_id = :articleId")
    boolean create(
            @Param("articleId") long articleId,
            @Param("subject") String subject,
            @Param("contents") String contents,
            @Param("html") Boolean html,
            @Param("completed") LocalDateTime completed,
            @Param("status") int status);

    @Modifying
    @Query("update community.board_articles set subject = :subject, contents = :contents, html = :html where board_article_id = :articleId")
    boolean update(
            @Param("articleId") long articleId,
            @Param("subject") String subject,
            @Param("contents") String contents,
            @Param("html") Boolean html);

    @Modifying
    @Query("update community.board_articles set `status` = :status where board_article_id = :articleId")
    boolean updateStatus(
            @Param("articleId") long articleId,
            @Param("status") int status);

    @Modifying
    @Query("update community.board_articles set `status` = :status, completed = :completed where board_article_id = :articleId")
    boolean updateStatusToComplete(
            @Param("articleId") long articleId,
            @Param("status") int status,
            @Param("completed") LocalDateTime completed);

    @Modifying
    @Query("update community.board_articles set `view` = `view` + 1 where board_article_id = :articleId")
    boolean updateViewCount(@Param("articleId") long articleId);

    @Modifying
    @Query("insert into community.board_article_view(board_article_id, member_id, created) values(:boardArticleViewId, :boardArticleId, :memberId, :created)")
    boolean createView(
            @Param("boardArticleId") long boardArticleId,
            @Param("memberId") long memberId,
            @Param("created") LocalDateTime created);

    @Modifying
    @Query("update community.board_articles set `like` = `like` + 1 where board_article_id = :articleId")
    boolean updateLikeCountByAdd(@Param("articleId") long articleId);

    @Modifying
    @Query("update community.board_articles set `like` = case when `like` = 0 then 0 else `like` - 1 end where board_article_id = :articleId")
    boolean updateLikeCountBySubtract(@Param("articleId") long articleId);

    @Modifying
    @Query("insert into community.board_article_likes(board_article_like_id, board_article_id, member_id, created) values(:boardArticleLikeId, :boardArticleId, :memberId, :created)")
    boolean createLike(
            @Param("boardArticleId") long boardArticleId,
            @Param("memberId") long memberId,
            @Param("created") LocalDateTime created);

    @Modifying
    @Query("delete from community.board_article_likes where board_article_id = :articleId and member_id = :memberId")
    boolean deleteLike(
            @Param("articleId") long articleId,
            @Param("memberId") long memberId);
}
