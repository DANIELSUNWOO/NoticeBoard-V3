package org.example.noticeboardv3.repository;

import org.example.noticeboardv3.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    // 로그인 식별자로 사용자 조회 — Security 인증(CustomUserDetailsService)에서 사용
    Optional<User> findByLoginId(String loginId);

    // 가입 시 loginId 중복 확인 → 있으면 DuplicateUserException (409)
    boolean existsByLoginId(String loginId);
}