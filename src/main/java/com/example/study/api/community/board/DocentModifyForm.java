package com.example.study.api.community.board;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record DocentModifyForm(
        @Schema(description = "도슨트 현재 비밀번호") String oldPassword,
        @Schema(description = "도슨트 새 비밀번호") String newPassword,
        @Schema(description = "도슨트 메일") String email,
        @Schema(description = "도슨트 휴대본 번호") String mobile,
        @Schema(description = "도슨트 주소") String address,
        @Schema(description = "도슨트 우편번호") String zipcode,
        @Schema(description = "도슨트 썸네일 http 주소") String thumbnail,
        @Schema(description = "도슨트 생일") LocalDate birthday,
        @Schema(description = "도슨트 직업") String job) {

//    public DocentEntity toEntity(DocentEntity oldDocentEntity) {
//        return new DocentEntity(
//                null,
//                oldDocentEntity.nickname(),
//                Sha512Utils.toBase64UrlEncoded(newPassword),
//                email,
//                mobile,
//                address,
//                thumbnail,
//                birthday,
//                job,
//                0, // Transaction...
//                0, // Transaction...
//                DocentActiveStatusType.ALIVE.code(),
//                null,
//                LocalDateTime.now());
//    }
}
