package org.example.noticeboardv3.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User implements UserDetails {  // [추가] UserDetails 구현 → Security의 인증 주체가 됨

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(name = "login_id", nullable = false, unique = true)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @Builder
    public User(String name, String loginId, String password, String email) {
        this.name = name;
        this.loginId = loginId;
        this.password = password;
        this.email = email;
        this.createdDate = LocalDateTime.now();
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    // ====== 여기서부터 UserDetails 구현부 ======

    // [추가] 이 사용자의 "권한 목록"을 Security가 이해하는 형태로 변환
    //        우리 Role(예: name="USER")을 → "ROLE_USER" 문자열 권한으로 바꿔줌
    //        [주의] Spring Security 관례상 권한 앞에 "ROLE_" 접두사를 붙임 (실습의 ROLE_ADMIN 체크와 일치시키기 위함)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList());
    }

    // [추가] Security가 "비밀번호 내놔"라고 할 때 → 우리 password 필드 반환
    @Override
    public String getPassword() {
        return this.password;
    }

    // [추가] Security가 "로그인 식별자 내놔"라고 할 때 → 우리는 loginId가 그 역할
    //        [주의] 메서드 이름은 getUsername()이지만, 우리 도메인에선 loginId를 의미함 (name 필드 아님!)
    @Override
    public String getUsername() {
        return this.loginId;
    }

    // [추가] 아래 4개는 "계정 상태" 질문들 — 만료/잠김/비번만료/활성화 여부.
    //        우리 명세엔 이런 상태 관리가 없으므로 전부 true("정상")로 고정.
    //        나중에 계정 정지 기능 같은 걸 넣으면 이 값들을 실제 필드와 연결하면 됨.
    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}