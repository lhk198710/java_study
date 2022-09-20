package com.example.study.simple.code.jpa.service;

import com.example.study.simple.code.jpa.entity.MemberEntity;
import com.example.study.simple.code.jpa.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MemberService {
    private MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public List<MemberEntity> findAll() {
        List<MemberEntity> members = new ArrayList<>();
        memberRepository.findAll().forEach(e -> members.add(e));
        return members;
    }

    public Optional<MemberEntity> findById(Long mbrNo) {
        Optional<MemberEntity> member = memberRepository.findById(mbrNo);
        return member;
    }

    public void deleteById(Long mbrNo) {
        memberRepository.deleteById(mbrNo);
    }

    public MemberEntity save(MemberEntity member) {
        memberRepository.save(member);
        return member;
    }

    public void updateById(Long mbrNo, MemberEntity member) {
        Optional<MemberEntity> e = memberRepository.findById(mbrNo);

        if (e.isPresent()) {
            e.get().setPid(member.getPid());
            e.get().setUsername(member.getUsername());
            e.get().setName(member.getName());
            memberRepository.save(member);
        }
    }
}
