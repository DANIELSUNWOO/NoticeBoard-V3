package org.example.noticeboardv3.controller;

import lombok.RequiredArgsConstructor;
import org.example.noticeboardv3.domain.User;
import org.example.noticeboardv3.dto.UserDto;
import org.example.noticeboardv3.exception.NotOwnerException;
import org.example.noticeboardv3.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원 가입 — 인증 불필요(SecurityConfig에서 permitAll), 201 Created + User 응답
    @PostMapping
    public ResponseEntity<UserDto.Response> createUser(
            @Valid @RequestBody UserDto.SignUpRequest request) {  // [추가] @Valid로 400 검증, 중첩 DTO로 받기
        User created = userService.createUser(request);
        // [핵심] 엔티티 → 응답 DTO 변환 (비밀번호 제외)
        return ResponseEntity.status(HttpStatus.CREATED).body(UserDto.Response.from(created));
    }

    // 회원 삭제 — 인증 필요, 본인 또는 ADMIN만, 204 No Content
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Integer id,
            @AuthenticationPrincipal User currentUser) {  // [추가] 현재 로그인한 사용자 (Security가 주입)
        // [핵심] 본인 또는 ADMIN만 삭제 가능 (403)
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        boolean isSelf = currentUser.getId().equals(id);
        if (!isAdmin && !isSelf) {
            throw new NotOwnerException("본인 계정 또는 관리자만 삭제할 수 있습니다.");
        }
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}