package org.example.noticeboardv3.service;

import lombok.RequiredArgsConstructor;
import org.example.noticeboardv3.domain.Post;
import org.example.noticeboardv3.domain.User;
import org.example.noticeboardv3.dto.PostDto;
import org.example.noticeboardv3.exception.NotOwnerException;
import org.example.noticeboardv3.exception.PostNotFoundException;
import org.example.noticeboardv3.exception.UserNotFoundException;
import org.example.noticeboardv3.repository.PostRepository;
import org.example.noticeboardv3.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;  // [추가] 작성자(User)를 찾기 위해 필요

    // 게시글 등록 — 작성자는 현재 로그인 사용자(loginId)로부터
    @Transactional
    public Post createPost(PostDto.Request request, String loginId) {
        User author = findUserByLoginId(loginId);  // 현재 로그인 사용자 = 작성자
        Post post = Post.builder()
                .title(request.title())
                .content(request.content())
                .user(author)  // [핵심] 작성자는 요청이 아니라 인증 정보에서
                .build();
        return postRepository.save(post);
    }

    // 게시글 수정 — 본인만 가능
    @Transactional
    public Post updatePost(Integer id, PostDto.Request request, String loginId) {
        Post post = findPostById(id);
        validateOwner(post, loginId);            // [핵심] 소유권 검증 → 본인 아니면 403
        post.update(request.title(), request.content());  // 더티 체킹 (save 불필요)
        return post;
    }

    // 게시글 삭제 — 본인만 가능 (딸린 댓글은 cascade로 함께 삭제됨)
    @Transactional
    public void deletePost(Integer id, String loginId) {
        Post post = findPostById(id);
        validateOwner(post, loginId);
        postRepository.delete(post);  // cascade=ALL, orphanRemoval → 댓글도 물리 삭제
    }

    // 게시글 목록 — 최신순 페이징 (읽기 전용)
    @Transactional(readOnly = true)
    public Page<Post> getPosts(Pageable pageable) {
//        return postRepository.findAllByOrderByCreatedDateDesc(pageable);
        return postRepository.findAllWithUser(pageable);
    }

    // 게시글 상세 — 없으면 404 (읽기 전용)
    @Transactional(readOnly = true)
    public Post getPost(Integer id) {
        return findPostById(id);
    }

    // === 내부 공통 메서드 ===

    private Post findPostById(Integer id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다. ID: " + id));
    }

    private User findUserByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + loginId));
    }

    // [핵심] 소유권 검증 — 게시글 작성자와 현재 로그인 사용자가 같은지
    private void validateOwner(Post post, String loginId) {
        if (!post.getUser().getLoginId().equals(loginId)) {
            throw new NotOwnerException("본인의 게시글만 수정/삭제할 수 있습니다.");
        }
    }
}