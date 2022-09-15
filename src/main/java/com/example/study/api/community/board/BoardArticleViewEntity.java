package com.example.study.api.community.board;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table(schema = "community", name = "board_article_view")
public record BoardArticleViewEntity(
        @Id Long boardArticleViewId,
        Long boardArticleId,
        Long memberId,
        LocalDateTime created) {
}
