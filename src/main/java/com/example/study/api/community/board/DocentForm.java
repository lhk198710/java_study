package com.example.study.api.community.board;

import com.example.study.api.utils.Sha512Utils;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DocentForm(
        @Schema(description = "도슨트 닉네임(아이디)") String nickname,
        @Schema(description = "도슨트 비번") String password,
        @Schema(description = "도슨트 메일") String email,
        @Schema(description = "도슨트 휴대본 번호") String mobile,
        @Schema(description = "도슨트 주소") String address,
        @Schema(description = "도슨트 우편번호") String zipcode,
        @Schema(description = "도슨트 썸네일 http 주소") String thumbnail,
        @Schema(description = "도슨트 생일") LocalDate birthday,
        @Schema(description = "도슨트 직업") String job) {

    public DocentEntity toEntity() {
        return new DocentEntity(
                null,
                nickname,
                Sha512Utils.toBase64UrlEncoded(password),
                email,
                mobile,
                address,
                zipcode,
                thumbnail,
                birthday,
                job,
                0,
                0,
                200,
                LocalDateTime.now(),
                null);
    }
}
