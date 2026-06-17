package org.example.noticeboardv3.service;

import lombok.RequiredArgsConstructor;
import org.example.noticeboardv3.domain.Comment;
import org.example.noticeboardv3.domain.Post;
import org.example.noticeboardv3.domain.User;
import org.example.noticeboardv3.dto.CommentDto;
import org.example.noticeboardv3.exception.CommentNotFoundException;
import org.example.noticeboardv3.exception.NotOwnerException;
import org.example.noticeboardv3.exception.PostNotFoundException;
import org.example.noticeboardv3.exception.UserNotFoundException;
import org.example.noticeboardv3.repository.CommentRepository;
import org.example.noticeboardv3.repository.PostRepository;
import org.example.noticeboardv3.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;   // [추가] 댓글이 달릴 게시글 존재 확인용
    private final UserRepository userRepository;    // [추가] 작성자(User) 조회용

    // 댓글 등록 — 작성자는 인증 정보에서, 게시글은 postId로 존재 확인
    @Transactional
    public Comment createComment(CommentDto.CreateRequest request, String loginId) {
        // 1. 게시글 존재 확인 → 없으면 등록 불가 (404)
        Post post = postRepository.findById(request.postId())
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다. ID: " + request.postId()));

        // 2. 작성자 = 현재 로그인 사용자
        User author = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + loginId));

        // 3. 댓글 생성 및 저장 (부모 둘: post + author)
        Comment comment = Comment.builder()
                .content(request.content())
                .post(post)
                .user(author)
                .build();
        return commentRepository.save(comment);
    }

    // 특정 게시글의 댓글 목록 — 삭제 안 된 것만, 작성일 오름차순 (읽기 전용)
    @Transactional(readOnly = true)
    public List<Comment> getCommentsByPostId(Integer postId) {
        return commentRepository.findByPostIdAndDelYnFalseOrderByCreatedDateAsc(postId);
    }

    // 댓글 소프트 삭제 — 본인만 가능
    @Transactional
    public void softDeleteComment(Integer id, String loginId) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException("댓글을 찾을 수 없습니다. ID: " + id));

        // 소유권 검증 → 본인 아니면 403
        if (!comment.getUser().getLoginId().equals(loginId)) {
            throw new NotOwnerException("본인의 댓글만 삭제할 수 있습니다.");
        }

        // [핵심] 실제 삭제가 아니라 플래그 변경 (더티 체킹으로 UPDATE 자동 실행)
        comment.softDelete();
    }
}