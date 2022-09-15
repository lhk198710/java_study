package com.example.study.api.community.board;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table(schema = "community", name = "board_article_likes")
public record BoardArticleLikeEntity(
        @Id Long boardArticleLikeId,
        Long boardArticleId,
        Long memberId,
        LocalDateTime created) {
}
