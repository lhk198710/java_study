package com.example.study.simple.code.jpa.repository;

import com.example.study.simple.code.jpa.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
}
