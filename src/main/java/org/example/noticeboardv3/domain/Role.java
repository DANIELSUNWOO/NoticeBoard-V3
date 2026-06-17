package org.example.noticeboardv3.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")  // 테이블명 "roles" — 실습 명세의 roles 테이블과 일치
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // 기본 생성자는 JPA 전용 → 외부 생성 차단
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // MySQL AUTO_INCREMENT
    private Integer id;

    @Column(nullable = false, unique = true)  // 역할 이름 중복 불가
    private String name;

    // new Role("USER") 형태를 위한 편의 생성자 (실습 6-5 힌트와 호환)
    public Role(String name) {
        this.name = name;
    }
}