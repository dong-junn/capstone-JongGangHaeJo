package jeiu.capstone.jongGangHaejo.service;

import jeiu.capstone.jongGangHaejo.domain.Like;
import jeiu.capstone.jongGangHaejo.exception.ResourceNotFoundException;
import jeiu.capstone.jongGangHaejo.repository.LikeRepository;
import jeiu.capstone.jongGangHaejo.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private LikeService likeService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // SecurityContext 설정
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Authentication 설정
        when(authentication.getName()).thenReturn("testUser");
    }

    @Test
    void 좋아요_토글_성공() {
        // given
        Long postId = 1L;
        String username = "testUser";

        when(postRepository.existsById(postId)).thenReturn(true);
        when(likeRepository.findByPostIdAndUsername(postId, username)).thenReturn(java.util.Optional.empty());

        // when
        likeService.toggleLike(postId);

        // then
        verify(likeRepository, times(1)).save(any(Like.class));
    }

    @Test
    void 존재하지_않는_게시물_좋아요_토글_실패() {
        // given
        Long postId = 999L;

        when(postRepository.existsById(postId)).thenReturn(false);

        // when & then
        assertThrows(ResourceNotFoundException.class, () -> likeService.toggleLike(postId));
    }
} 