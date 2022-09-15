package com.example.study.api.community.board;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table(schema="docent", name="docent_followers")
public record DocentFollowerEntity(@Id Long docentFollowId,
                                   Long docentId,
                                   Long memberId,
                                   @CreatedDate LocalDateTime created) {
}
