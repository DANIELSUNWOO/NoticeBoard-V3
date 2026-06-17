package org.example.noticeboardv3.repository;

import org.example.noticeboardv3.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    // 회원가입 시 "USER" 역할을 찾을 때 사용 (없으면 새로 만들 예정 — 실습 6-5)
    Optional<Role> findByName(String name);
}