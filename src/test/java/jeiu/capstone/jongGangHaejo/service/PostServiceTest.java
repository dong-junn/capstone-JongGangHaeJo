package jeiu.capstone.jongGangHaejo.service;

import jeiu.capstone.jongGangHaejo.domain.Post;
import jeiu.capstone.jongGangHaejo.dto.request.PostCreateDto;
import jeiu.capstone.jongGangHaejo.exception.ResourceNotFoundException;
import jeiu.capstone.jongGangHaejo.exception.common.CommonErrorCode;
import jeiu.capstone.jongGangHaejo.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.dao.DataAccessException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private FileService fileService;

    @InjectMocks
    private PostService postService;

    @Captor
    private ArgumentCaptor<Post> postCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 게시물_생성_테스트() throws Exception {
        // given
        PostCreateDto dto = new PostCreateDto();
        dto.setTitle("Test Title");
        dto.setContent("Test Content");
        dto.setTeam("Test Team");
        dto.setYoutubelink("https://youtube.com/test");

        MultipartFile file1 = mock(MultipartFile.class);
        MultipartFile file2 = mock(MultipartFile.class);
        List<MultipartFile> files = Arrays.asList(file1, file2);

        when(fileService.uploadFiles(files)).thenReturn(Arrays.asList(100L, 101L));

        Post savedPost = Post.builder()
                .postid(1L)
                .username("testUser")
                .title("Test Title")
                .content("Test Content")
                .team("Test Team")
                .youtubelink("https://youtube.com/test")
                .fileIds(Arrays.asList(100L, 101L))
                .viewCount(0L)
                .build();

        when(postRepository.save(any(Post.class))).thenReturn(savedPost);

        // when
        postService.createPost(dto, files);

        // then
        verify(fileService, times(1)).uploadFiles(files);
        verify(postRepository, times(1)).save(postCaptor.capture());

        Post capturedPost = postCaptor.getValue();
        assertEquals("Test Title", capturedPost.getTitle());
        assertEquals("Test Content", capturedPost.getContent());
        assertEquals("Test Team", capturedPost.getTeam());
        assertEquals("https://youtube.com/test", capturedPost.getYoutubelink());
        assertEquals(Arrays.asList(100L, 101L), capturedPost.getFileIds());
        assertEquals(0L, capturedPost.getViewCount());
    }

    @Test
    void 단일_게시물_조회_및_조회수_증가_테스트() {
        // given
        Long postId = 1L;
        Post post = Post.builder()
                .postid(postId)
                .username("testUser")
                .title("Test Title")
                .content("Test Content")
                .team("Test Team")
                .youtubelink("https://youtube.com/test")
                .fileIds(Arrays.asList(100L, 101L))
                .viewCount(5L)
                .build();

        when(postRepository.findById(postId)).thenReturn(java.util.Optional.of(post));
        when(postRepository.incrementViewCount(postId)).thenReturn(1);

        // when
        Post foundPost = postService.getSinglePost(postId);

        // then
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).incrementViewCount(postId);

        assertEquals("Test Title", foundPost.getTitle());
        assertEquals(5L, foundPost.getViewCount()); // viewCount는 이미 엔티티에 저장된 값
    }

    @Test
    void 단일_게시물_조회_없는_경우_예외_발생() {
        // given
        Long postId = 1L;
        when(postRepository.findById(postId)).thenReturn(java.util.Optional.empty());

        // when & then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> postService.getSinglePost(postId)
        );

        assertEquals("게시물을 찾을 수 없습니다. 게시물 번호: " + postId, exception.getMessage());
        assertEquals(CommonErrorCode.RESOURCE_NOT_FOUND, exception.getErrorCode());

        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, never()).incrementViewCount(anyLong());
    }

    @Test
    void 단일_게시물_조회_데이터베이스_예외_발생() {
        // given
        Long postId = 1L;
        when(postRepository.findById(postId)).thenThrow(new DataAccessException("DB error") {});

        // when & then
        DataAccessException exception = assertThrows(
                DataAccessException.class,
                () -> postService.getSinglePost(postId)
        );

        assertEquals("DB error", exception.getMessage());

        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, never()).incrementViewCount(anyLong());
    }


}
