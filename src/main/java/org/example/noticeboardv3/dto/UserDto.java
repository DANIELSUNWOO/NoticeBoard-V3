package org.example.noticeboardv3.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.example.noticeboardv3.domain.User;

import java.util.List;

public class UserDto {

    // 회원 가입 요청
    public record SignUpRequest(
            @NotBlank(message = "이름은 필수입니다.")     String name,
            @NotBlank(message = "loginId는 필수입니다.")  String loginId,
            @NotBlank(message = "비밀번호는 필수입니다.")  String password,
            @NotBlank(message = "이메일은 필수입니다.")
            @Email(message = "이메일 형식이 올바르지 않습니다.") String email
            // role은 요청에 없음 — 서버가 자동으로 "USER" 부여
    ) {}

    // 회원 응답 — password 제외, roles 배열 포함
    public record Response(
            Integer id,
            String name,
            String loginId,
            String email,
            List<RoleInfo> roles  // [변경] 단일 role(String) → roles 배열 (Role 엔티티 방식)
    ) {
        // 역할 하나를 {id, name}으로 표현하는 중첩 record (테스트 문서 JSON과 일치)
        public record RoleInfo(Integer id, String name) {}

        public static Response from(User user) {
            // [주의] user.getRoles() 접근 — roles는 EAGER라 이미 로딩돼 있어 안전
            List<RoleInfo> roleInfos = user.getRoles().stream()
                    .map(role -> new RoleInfo(role.getId(), role.getName()))
                    .toList();
            return new Response(
                    user.getId(),
                    user.getName(),
                    user.getLoginId(),
                    user.getEmail(),
                    roleInfos
            );
        }
    }
}