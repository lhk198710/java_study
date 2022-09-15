package com.example.study.api.community.board;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table(schema = "community", name = "board_articles")
public record BoardArticleEntity(
        @Id Long boardArticleId,
        Long refArticleId,
        Long boardId,
        String subject,
        String contents,
        Boolean html,
        String thumbnail,
        Integer view,
        Integer like,
        String creator,
        LocalDateTime created,
        LocalDateTime completed,
        Integer status) {
}
