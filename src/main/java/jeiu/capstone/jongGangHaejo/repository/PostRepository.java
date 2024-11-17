package jeiu.capstone.jongGangHaejo.repository;

import jakarta.transaction.Transactional;
import jeiu.capstone.jongGangHaejo.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {
    /**
     * 특정 게시물의 조회수를 1 증가시킵니다.
     *
     * @param postId 조회수를 증가시킬 게시물의 ID
     * @return 업데이트된 레코드 수
     */
    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.postid = :postId")
    int incrementViewCount(Long postId);
}
