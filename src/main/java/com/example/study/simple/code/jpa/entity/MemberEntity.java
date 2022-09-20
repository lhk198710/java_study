package com.example.study.simple.code.jpa.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Builder // 빌더를 사용할 수 있게 함
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(schema = "test_db", name = "member")
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long pid;

    @Column(nullable = false, unique = true, length = 30)
    private String username;

    @Column(nullable = false, length = 100)
    private String name;

    public MemberEntity(String username, String name) {
        this.username = username;
        this.name = name;
    }
}
