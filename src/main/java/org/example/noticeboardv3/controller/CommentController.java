package org.example.noticeboardv3.controller;

import lombok.RequiredArgsConstructor;
import org.example.noticeboardv3.domain.Comment;
import org.example.noticeboardv3.domain.User;
import org.example.noticeboardv3.dto.CommentDto;
import org.example.noticeboardv3.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 등록 — 201 Created
    @PostMapping
    public ResponseEntity<CommentDto.Response> createComment(
            @Valid @RequestBody CommentDto.CreateRequest request,
            @AuthenticationPrincipal User currentUser) {
        Comment created = commentService.createComment(request, currentUser.getLoginId());
        return ResponseEntity.status(HttpStatus.CREATED).body(CommentDto.Response.from(created));
    }

    // 특정 게시글의 댓글 목록 — 200 OK
    // [핵심] postId를 쿼리 파라미터로 받음 (?postId=1). 게시글과 달리 페이징 없이 List.
    //        @RequestParam은 기본 required=true → postId 없으면 400 (명세: postId 필수)
    @GetMapping
    public ResponseEntity<List<CommentDto.Response>> getComments(
            @RequestParam Integer postId) {
        List<Comment> comments = commentService.getCommentsByPostId(postId);
        // List<Comment> → List<CommentDto.Response> 변환
        List<CommentDto.Response> response = comments.stream()
                .map(CommentDto.Response::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    // 댓글 삭제(소프트) — 본인만, 204 No Content
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Integer id,
            @AuthenticationPrincipal User currentUser) {
        commentService.softDeleteComment(id, currentUser.getLoginId());
        return ResponseEntity.noContent().build();
    }
}