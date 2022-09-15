package com.example.study.api.community.board;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table(schema="docent", name="docent_likes")
public record DocentLikeEntity(@Id Long docentLikeId,
                               Long docentId,
                               Long memberId,
                               @CreatedDate LocalDateTime created) {
}
