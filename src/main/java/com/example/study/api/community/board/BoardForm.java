package com.example.study.api.community.board;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record BoardForm(
        @Schema(description = "게시판 코드") String boardCode,
        @Schema(description = "게시판 이름") String name,
        @Schema(description = "상태") int status) {

    public BoardEntity toEntity() {
        return new BoardEntity(null, boardCode, name, status, LocalDateTime.now());
    }
}
