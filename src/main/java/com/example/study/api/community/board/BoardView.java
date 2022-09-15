package com.example.study.api.community.board;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record BoardView(
        @Schema(description = "게시판 id") Long boardId,
        @Schema(description = "게시판 코드") String boardCode,
        @Schema(description = "게시판 이름") String name,
        @Schema(description = "상태") int status,
        @Schema(description = "생성일시") LocalDateTime created) {

    public static BoardView of(BoardEntity entity) {
        return new BoardView(
                entity.boardId(),
                entity.boardCode(),
                entity.name(),
                entity.status(),
                entity.created());
    }
}
