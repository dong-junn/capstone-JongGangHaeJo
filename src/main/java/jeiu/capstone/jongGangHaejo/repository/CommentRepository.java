package jeiu.capstone.jongGangHaejo.repository;

import jeiu.capstone.jongGangHaejo.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 특정 게시글의 댓글 목록을 생성일시 기준 내림차순으로 조회
    List<Comment> findByPostIdOrderByCreatedAtDesc(Long postId);
} 