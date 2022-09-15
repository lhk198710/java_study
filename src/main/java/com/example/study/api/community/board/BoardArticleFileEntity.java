package com.example.study.api.community.board;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(schema = "community", name = "board_article_media_files")
public record BoardArticleFileEntity(
        @Id Long boardArticleFileId,
        long boardArticleId,
        long fileId) {
}
