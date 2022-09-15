package com.example.study.api.community.board;

import io.swagger.v3.oas.annotations.media.Schema;

public record BoardArticleModifyForm(
        @Schema(description = "제목") String subject,
        @Schema(description = "내용") String contents,
        @Schema(description = "html 여부") Boolean html) {
}
