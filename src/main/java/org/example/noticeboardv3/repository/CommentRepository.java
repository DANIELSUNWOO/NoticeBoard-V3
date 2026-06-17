package org.example.noticeboardv3.repository;

import org.example.noticeboardv3.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    // 특정 게시글의 댓글 목록 — 삭제 안 된 것만(delYn=false), 작성일 오름차순
    // [결정] 명세의 세 조건(postId 일치 + del_yn=false + 작성일 오름차순)을 메서드 이름 하나에 모두 표현.
    List<Comment> findByPostIdAndDelYnFalseOrderByCreatedDateAsc(Integer postId);
}