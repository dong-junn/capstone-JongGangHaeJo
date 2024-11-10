package jeiu.capstone.jongGangHaejo.service;

import jeiu.capstone.jongGangHaejo.domain.Comment;
import jeiu.capstone.jongGangHaejo.dto.request.CommentCreateDto;
import jeiu.capstone.jongGangHaejo.dto.response.CommentResponseDto;
import jeiu.capstone.jongGangHaejo.exception.ResourceNotFoundException;
import jeiu.capstone.jongGangHaejo.exception.UnauthorizedException;
import jeiu.capstone.jongGangHaejo.repository.CommentRepository;
import jeiu.capstone.jongGangHaejo.repository.PostRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        // SecurityContextHolder에 SecurityContext 설정
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        // SecurityContextHolder 정리
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("댓글 생성 성공")
    void 댓글_생성_성공() {
        // given
        Long postId = 1L;
        CommentCreateDto dto = new CommentCreateDto();
        dto.setContent("테스트 댓글");

        // SecurityContext와 Authentication 스텁 설정
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");

        given(postRepository.existsById(postId)).willReturn(true);
        given(commentRepository.save(any(Comment.class)))
                .willAnswer(invocation -> {
                    Comment comment = invocation.getArgument(0);
                    return Comment.builder()
                            .content(comment.getContent())
                            .username(comment.getUsername())
                            .postId(comment.getPostId())
                            .build();
                });

        // when
        CommentResponseDto response = commentService.createComment(postId, dto);

        // then
        assertThat(response.getContent()).isEqualTo("테스트 댓글");
        assertThat(response.getUsername()).isEqualTo("testUser");
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("존재하지 않는 게시글에 댓글 작성 시 예외 발생")
    void 존재하지_않는_게시글_댓글작성() {
        // given
        Long postId = 999L;
        CommentCreateDto dto = new CommentCreateDto();
        dto.setContent("테스트 댓글");

        given(postRepository.existsById(postId)).willReturn(false);

        // when & then
        assertThrows(ResourceNotFoundException.class,
                () -> commentService.createComment(postId, dto));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 목록 조회")
    void 댓글_목록_조회() {
        // given
        Long postId = 1L;
        List<Comment> comments = Arrays.asList(
                Comment.builder()
                        .content("첫번째 댓글")
                        .username("user1")
                        .postId(postId)
                        .build(),
                Comment.builder()
                        .content("두번째 댓글")
                        .username("user2")
                        .postId(postId)
                        .build()
        );

        given(commentRepository.findByPostIdOrderByCreatedAtDesc(postId))
                .willReturn(comments);

        // when
        List<CommentResponseDto> result = commentService.getComments(postId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getContent()).isEqualTo("첫번째 댓글");
        assertThat(result.get(1).getContent()).isEqualTo("두번째 댓글");
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    void 댓글_삭제_성공() {
        // given
        Long commentId = 1L;
        Comment comment = Comment.builder()
                .content("테스트 댓글")
                .username("testUser")
                .postId(1L)
                .build();

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

        // SecurityContext와 Authentication 스텁 설정
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");

        // when
        commentService.deleteComment(commentId);

        // then
        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    @DisplayName("타인의 댓글 삭제 시도시 예외 발생")
    void 타인의_댓글_삭제_시도() {
        // given
        Long commentId = 1L;
        Comment comment = Comment.builder()
                .content("테스트 댓글")
                .username("otherUser")
                .postId(1L)
                .build();

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

        // SecurityContext와 Authentication 스텁 설정
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");

        // when & then
        assertThrows(UnauthorizedException.class,
                () -> commentService.deleteComment(commentId));
    }
}
