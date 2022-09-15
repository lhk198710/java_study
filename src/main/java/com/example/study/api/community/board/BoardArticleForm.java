package com.example.study.api.community.board;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public record BoardArticleForm(
        @Schema(description = "게시판 id") Long boardId,
        @Schema(description = "제목") String subject,
        @Schema(description = "내용") String contents,
        @Schema(description = "html 여부") Boolean html,
        @Schema(description = "생성자") String creator,
        @Schema(description = "첨부파일 목록") List<Long> fileIds) {

    public BoardArticleEntity toEntityInitial() {
        return new BoardArticleEntity(null, null, boardId, subject, contents, html, null, 0, 0, creator, LocalDateTime.now(), null, 200);
    }
}
