package com.example.study.api.community.board;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table(schema = "community", name = "boards")
public record BoardEntity(
        @Id Long boardId,
        String boardCode,
        String name,
        int status,
        LocalDateTime created) {
}
