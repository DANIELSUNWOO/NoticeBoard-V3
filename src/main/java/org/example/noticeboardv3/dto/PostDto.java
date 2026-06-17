package org.example.noticeboardv3.dto;

import jakarta.validation.constraints.NotBlank;
import org.example.noticeboardv3.domain.Post;
import org.example.noticeboardv3.domain.User;

import java.time.LocalDateTime;

public class PostDto {

    // 등록·수정 공용 요청 (둘 다 {title, content})
    public record Request(
            @NotBlank(message = "제목은 필수입니다.") String title,
            @NotBlank(message = "내용은 필수입니다.") String content
    ) {}

    public record Response(
            Integer id,
            String title,
            String content,
            Author author,          // [변경] 평면 authorId/authorName → 중첩 author 객체 (테스트 문서 JSON과 일치)
            LocalDateTime createdAt, // [변경] 엔티티는 createdDate지만 JSON 키는 createdAt
            LocalDateTime updatedAt
    ) {
        // 작성자 정보 — 비밀번호 등 민감 정보 제외, 필요한 것만
        public record Author(Integer id, String name, String loginId) {}

        public static Response from(Post post) {
            // [주의] post.getUser() 접근 시 LAZY 로딩 발생 → 목록 변환 시 N+1이 터지는 지점
            User u = post.getUser();
            return new Response(
                    post.getId(),
                    post.getTitle(),
                    post.getContent(),
                    new Author(u.getId(), u.getName(), u.getLoginId()),
                    post.getCreatedDate(),  // 엔티티 createdDate → JSON createdAt으로 매핑
                    post.getUpdatedDate()
            );
        }
    }
}