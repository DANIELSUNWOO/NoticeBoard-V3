package org.example.noticeboardv3.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.noticeboardv3.domain.Comment;
import org.example.noticeboardv3.domain.User;

import java.time.LocalDateTime;

public class CommentDto {

    // 댓글 등록 요청
    public record CreateRequest(
            @NotBlank(message = "댓글 내용은 필수입니다.") String content,
            @NotNull(message = "postId는 필수입니다.")    Integer postId
    ) {}

    public record Response(
            Integer id,
            String content,
            Author author,          // 게시글과 동일한 형태의 작성자 객체
            Integer postId,
            LocalDateTime createdAt
            // delYn은 응답에서 제외 — 어차피 삭제 안 된 댓글만 조회되므로 항상 false
    ) {
        public record Author(Integer id, String name, String loginId) {}

        public static Response from(Comment comment) {
            User u = comment.getUser();
            return new Response(
                    comment.getId(),
                    comment.getContent(),
                    new Author(u.getId(), u.getName(), u.getLoginId()),
                    comment.getPost().getId(),
                    comment.getCreatedDate()
            );
        }
    }
}