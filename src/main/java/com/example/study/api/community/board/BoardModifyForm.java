package com.example.study.api.community.board;

import io.swagger.v3.oas.annotations.media.Schema;

public record BoardModifyForm(
        @Schema(description = "게시판 코드") String boardCode,
        @Schema(description = "게시판 이름") String name,
        @Schema(description = "상태") int status) {
}
