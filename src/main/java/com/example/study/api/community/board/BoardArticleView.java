package com.example.study.api.community.board;



import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record BoardArticleView(
        @Schema(description = "게시글 id") Long boardArticleId,
        @Schema(description = "참조 게시글 id") Long refArticleId,
        @Schema(description = "게시판 id") long boardId,
        @Schema(description = "제목") String subject,
        @Schema(description = "내용") String contents,
        @Schema(description = "html 여부") boolean html,
        @Schema(description = "썸네일") String thumbnail,
        @Schema(description = "조회 수") int view,
        @Schema(description = "좋아요 수") int like,
        @Schema(description = "생성자") String creator,
        @Schema(description = "생성일시") LocalDateTime created,
        @Schema(description = "완료일시") LocalDateTime completed,
        @Schema(description = "상태") int status) {

    public static BoardArticleView of(BoardArticleEntity entity) {
        return new BoardArticleView(
                entity.boardArticleId(),
                entity.refArticleId(),
                entity.boardId(),
                entity.subject(),
                entity.contents(),
                entity.html(),
                entity.thumbnail(),
                entity.view(),
                entity.like(),
                entity.creator(),
                entity.created(),
                entity.completed(),
                entity.status());
    }
}
