package org.example.noticeboardv3.controller;

import lombok.RequiredArgsConstructor;
import org.example.noticeboardv3.domain.Post;
import org.example.noticeboardv3.domain.User;
import org.example.noticeboardv3.dto.PostDto;
import org.example.noticeboardv3.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 게시글 등록 — 201 Created
    @PostMapping
    public ResponseEntity<PostDto.Response> createPost(
            @Valid @RequestBody PostDto.Request request,
            @AuthenticationPrincipal User currentUser) {  // 현재 로그인 사용자
        Post created = postService.createPost(request, currentUser.getLoginId());  // loginId를 서비스로
        return ResponseEntity.status(HttpStatus.CREATED).body(PostDto.Response.from(created));
    }

    // 게시글 수정 — 본인만, 200 OK
    @PutMapping("/{id}")
    public ResponseEntity<PostDto.Response> updatePost(
            @PathVariable Integer id,
            @Valid @RequestBody PostDto.Request request,
            @AuthenticationPrincipal User currentUser) {
        Post updated = postService.updatePost(id, request, currentUser.getLoginId());
        return ResponseEntity.ok(PostDto.Response.from(updated));
    }

    // 게시글 삭제 — 본인만, 204 No Content
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Integer id,
            @AuthenticationPrincipal User currentUser) {
        postService.deletePost(id, currentUser.getLoginId());
        return ResponseEntity.noContent().build();
    }

    // 게시글 목록 — 페이징, 200 OK
    // [핵심] Pageable을 통째로 받음 → ?page=0&size=10 을 Spring이 자동 변환
    //        @PageableDefault로 기본값(size=10) 지정. 정렬은 서비스의 리포지토리 메서드가 보장.
    @GetMapping
    public ResponseEntity<Page<PostDto.Response>> getPosts(
            @PageableDefault(size = 10) Pageable pageable) {
        Page<Post> posts = postService.getPosts(pageable);
        // [핵심] Page<Post> → Page<PostDto.Response> 변환. .map()으로 각 요소를 DTO로.
        Page<PostDto.Response> response = posts.map(PostDto.Response::from);
        return ResponseEntity.ok(response);
    }

    // 게시글 상세 — 200 OK (없으면 서비스에서 404)
    @GetMapping("/{id}")
    public ResponseEntity<PostDto.Response> getPost(@PathVariable Integer id) {
        Post post = postService.getPost(id);
        return ResponseEntity.ok(PostDto.Response.from(post));
    }
}