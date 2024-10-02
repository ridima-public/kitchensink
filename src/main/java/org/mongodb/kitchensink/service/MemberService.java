package org.mongodb.kitchensink.service;


import org.mongodb.kitchensink.data.MemberRepository;
import org.mongodb.kitchensink.model.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    public Optional<Member> findById(String id) {
        return memberRepository.findById(id);
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public Member createMember(Member member) {
        return memberRepository.save(member);
    }

    public List<Member> findAllOrderedByName() {
        return memberRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    public List<Member> findAll() {
       List<Member> members = memberRepository.findAll();
         return members;
    }
}
