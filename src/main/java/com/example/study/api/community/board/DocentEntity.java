package com.example.study.api.community.board;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(schema="docent", name="docents")
public record DocentEntity(@Id Long docentId,
                           String nickname,
                           String password,
                           String email,
                           String mobile,
                           String address,
                           String zipcode,
                           String thumbnail,
                           LocalDate birthday,
                           String job,
                           Integer like,
                           Integer follower,
                           Integer status,
                           @CreatedDate LocalDateTime created,
                           @LastModifiedDate LocalDateTime updated) {
}
