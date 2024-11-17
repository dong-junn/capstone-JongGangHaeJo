package jeiu.capstone.jongGangHaejo.repository;

import jeiu.capstone.jongGangHaejo.config.QueryDslTestConfig;
import jeiu.capstone.jongGangHaejo.domain.Comment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(QueryDslTestConfig.class)
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Test
    @DisplayName("댓글 DB 저장 테스트")
    void 댓글_저장_테스트() {
        // given
        Comment comment = Comment.builder()
                .content("테스트 댓글")
                .username("testUser")
                .postId(1L)
                .build();

        // when
        Comment savedComment = commentRepository.save(comment);

        // then
        assertThat(savedComment.getCommentId()).isNotNull();
        assertThat(savedComment.getContent()).isEqualTo("테스트 댓글");
        assertThat(savedComment.getUsername()).isEqualTo("testUser");
        assertThat(savedComment.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("게시글별 댓글 목록 조회 테스트")
    void 게시글별_댓글_목록_조회() {
        // given
        Long postId = 1L;
        Comment comment1 = Comment.builder()
                .content("첫번째 댓글")
                .username("user1")
                .postId(postId)
                .build();
        Comment comment2 = Comment.builder()
                .content("두번째 댓글")
                .username("user2")
                .postId(postId)
                .build();

        commentRepository.save(comment1);
        commentRepository.save(comment2);

        // when
        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtDesc(postId);

        // then
        assertThat(comments).hasSize(2);
        assertThat(comments.get(0).getContent()).isEqualTo("두번째 댓글");
        assertThat(comments.get(1).getContent()).isEqualTo("첫번째 댓글");
    }
} 