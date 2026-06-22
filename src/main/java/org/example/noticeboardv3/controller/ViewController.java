package org.example.noticeboardv3.controller;

import lombok.RequiredArgsConstructor;
import org.example.noticeboardv3.domain.User;
import org.example.noticeboardv3.dto.PostDto;

import org.example.noticeboardv3.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor   // (6.22 add)
public class ViewController {

    private final PostService postService;   // 게시글 데이터를 가져오기 위해 (6.22 add)

    // 로그인 화면 — GET /view/login
    @GetMapping("/view/login")
    public String loginPage() {
        // "login"을 반환 → templates/login.html 파일을 찾아서 보여줌
        return "login";
    }

    // 게시글 목록 화면 — 로그인 성공 후 도착지
    @GetMapping("/view/posts")
    public String postsPage(
            @PageableDefault(size = 10) Pageable pageable,
            @AuthenticationPrincipal User currentUser,   // 현재 로그인한 사용자
            Model model) {                                // 화면에 데이터를 담아 전달
        // API와 똑같이 서비스 호출 → 엔티티를 DTO로 변환
        Page<PostDto.Response> posts = postService.getPosts(pageable)
                .map(PostDto.Response::from);

        // [핵심] model에 담으면 Thymeleaf가 화면에서 꺼내 쓸 수 있음
        model.addAttribute("posts", posts);
        model.addAttribute("currentUser", currentUser);  // 헤더에 "OOO님" 표시용

        return "posts";   // templates/posts.html 렌더링
    }
}