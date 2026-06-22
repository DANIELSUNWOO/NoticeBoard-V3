package org.example.noticeboardv3.repository;

import org.example.noticeboardv3.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Integer> {

    // 기존: findAllByOrderByCreatedDateDesc — 게시글만 가져오고 작성자는 나중에 따로 (N+1)
    // [변경] fetch join으로 게시글 + 작성자를 한 번에 가져옴 (N+1 해결)
    @Query(value = "SELECT p FROM Post p JOIN FETCH p.user ORDER BY p.createdDate DESC",
            countQuery = "SELECT COUNT(p) FROM Post p")
    Page<Post> findAllWithUser(Pageable pageable);

    // 제목에 keyword가 포함된 게시글 검색 (페이징 + 작성자 fetch join)
// [결정] 검색 결과도 목록처럼 작성자를 함께 보여주므로 fetch join (N+1 방지)
//        LIKE '%keyword%' 검색을 위해 CONCAT 사용
    @Query(value = "SELECT p FROM Post p JOIN FETCH p.user " +
            "WHERE p.title LIKE CONCAT('%', :keyword, '%') " +
            "ORDER BY p.createdDate DESC",
            countQuery = "SELECT COUNT(p) FROM Post p WHERE p.title LIKE CONCAT('%', :keyword, '%')")
    Page<Post> searchByTitle(@Param("keyword") String keyword, Pageable pageable);
}