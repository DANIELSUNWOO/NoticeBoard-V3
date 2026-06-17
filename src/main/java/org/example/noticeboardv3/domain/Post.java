package org.example.noticeboardv3.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

//import org.example.noticeboardv3.domain.Comment;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")  // 내용은 길 수 있으니 TEXT 타입
    private String content;

    @Column(name = "created_date", updatable = false)  // 작성일 — 생성 후 변경 불가
    private LocalDateTime createdDate;

    @Column(name = "updated_date")  // 수정일 — 수정 시 갱신됨
    private LocalDateTime updatedDate;

    // === 작성자 연관관계 (ManyToOne) ===
    // [결정] 여러 게시글이 한 명의 작성자에 속함 → ManyToOne
    //        필드명은 의미가 또렷하게 "user" (실습 6-6 힌트의 post.getUser()와 일치)
    //        [주의] fetch = LAZY — 목록 조회 시 N+1 방지의 핵심. (작성자를 꼭 필요할 때만 로딩)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)  // FK 컬럼명 user_id (앞 SQL과 일치)
    private User user;

    // === 댓글 연관관계 (OneToMany, 양방향) ===
    // [결정] 게시글 삭제 시 댓글도 함께 삭제(CASCADE) → 명세의 "게시글 삭제 시 관련 댓글도 함께 삭제"
    //        cascade = ALL + orphanRemoval = true 로 물리적 삭제 보장
    //        mappedBy = "post" → 이 관계의 주인은 Comment 쪽(Comment.post 필드)이라는 뜻
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Builder
    public Post(String title, String content, User user) {
        this.title = title;
        this.content = content;
        this.user = user;
        this.createdDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();  // 생성 시점엔 작성일=수정일
    }

    // === 수정 메서드 (더티 체킹용) ===
    // [결정] setter 대신 의미 있는 메서드. 명세대로 title/content만 수정, 작성자·작성일은 불변.
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
        this.updatedDate = LocalDateTime.now();  // 수정 시각 갱신
    }
}