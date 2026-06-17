package org.example.noticeboardv3.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // === 소프트 삭제 플래그 ===
    // [결정] del_yn — 댓글 직접 삭제 시 실제로 지우지 않고 이 값을 true로 변경
    //        [주의] 기본값 false로 초기화 (생성 시점엔 삭제 안 된 상태)
    @Column(name = "del_yn", nullable = false)
    private boolean delYn = false;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    // === 게시글 연관관계 (ManyToOne) — Post의 mappedBy="post"가 가리키는 그 필드 ===
    // [결정] 여러 댓글이 한 게시글에 속함 → ManyToOne. 이쪽이 관계의 주인(FK 보유).
    //        [주의] LAZY — 댓글 조회 시 게시글을 즉시 로딩하지 않음 (N+1 방지)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)  // FK 컬럼 post_id
    private Post post;

    // === 작성자 연관관계 (ManyToOne) ===
    // [결정] 필드명 "user" (Post와 일관, 실습 힌트의 comment.getUser()와 호환)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)  // FK 컬럼 user_id
    private User user;

    @Builder
    public Comment(String content, Post post, User user) {
        this.content = content;
        this.post = post;
        this.user = user;
        this.delYn = false;  // 명시적으로 삭제 안 된 상태로 시작
        this.createdDate = LocalDateTime.now();
    }

    // === 소프트 삭제 메서드 (더티 체킹용) ===
    // [결정] delete()라는 이름이지만 실제 동작은 플래그 변경(UPDATE). 데이터는 남음.
    public void softDelete() {
        this.delYn = true;
    }
}