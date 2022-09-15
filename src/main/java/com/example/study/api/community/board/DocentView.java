package com.example.study.api.community.board;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DocentView(String nickname,
                         String password,
                         String email,
                         String mobile,
                         String address,
                         String zipcode,
                         String thumbnail,
                         LocalDate birthday,
                         String job,
                         int like,
                         int follower,
                         int status,
                         LocalDateTime created,
                         LocalDateTime updated) {

    public static DocentView of(DocentEntity docentEntity) {
        return new DocentView(docentEntity.nickname(),
                docentEntity.password(),
                docentEntity.email(),
                docentEntity.mobile(),
                docentEntity.address(),
                docentEntity.zipcode(),
                docentEntity.thumbnail(),
                docentEntity.birthday(),
                docentEntity.job(),
                docentEntity.like(),
                docentEntity.follower(),
                docentEntity.status(),
                docentEntity.created(),
                docentEntity.updated());
    }
}
