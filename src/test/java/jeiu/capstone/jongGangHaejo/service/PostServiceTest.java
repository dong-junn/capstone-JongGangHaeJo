package jeiu.capstone.jongGangHaejo.service;

import jeiu.capstone.jongGangHaejo.domain.Post;
import jeiu.capstone.jongGangHaejo.dto.request.PostCreateDto;
import jeiu.capstone.jongGangHaejo.exception.InvalidFileNameException;
import jeiu.capstone.jongGangHaejo.exception.ResourceNotFoundException;
import jeiu.capstone.jongGangHaejo.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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

        MockMultipartFile file1 = new MockMultipartFile(
                "files",
                "file1.txt",
                "text/plain",
                "File 1 Content".getBytes()
        );

        MockMultipartFile file2 = new MockMultipartFile(
                "files",
                "file2.txt",
                "text/plain",
                "File 2 Content".getBytes()
        );

        List<MultipartFile> files = Arrays.asList(file1, file2);

        // Mock uploadFiles to return mock file IDs
        when(fileService.uploadFiles(anyList()))
                .thenReturn(Arrays.asList(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE,
                        UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE));

        // Mock save post
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
            Post post = invocation.getArgument(0);
            post.setPostid(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE);
            return post;
        });

        // when
        postService.createPost(dto, files);

        // then
        verify(fileService, times(1)).uploadFiles(files);
        verify(postRepository, times(1)).save(postCaptor.capture());

        Post savedPost = postCaptor.getValue();
        assertEquals("Test Title", savedPost.getTitle());
        assertEquals("Test Content", savedPost.getContent());
        assertEquals("Test Team", savedPost.getTeam());
        assertEquals("https://youtube.com/test", savedPost.getYoutubelink());
        assertEquals(2, savedPost.getFileIds().size());

        for (Long fileId : savedPost.getFileIds()) {
            assertNotNull(fileId);  // 파일 ID가 null이 아닌지 검증
        }
    }

    @Test
    void 첨부파일_없는_게시물_업로드_테스트() throws Exception {
        // given
        PostCreateDto dto = new PostCreateDto();
        dto.setTitle("Test Title");
        dto.setContent("Test Content");
        dto.setTeam("Test Team");
        dto.setYoutubelink("https://youtube.com/test");

        MockMultipartFile emptyFile = new MockMultipartFile(
                "files",
                "",
                "text/plain",
                new byte[0]
        );

        List<MultipartFile> files = Arrays.asList(emptyFile);

        // Mock uploadFiles to return empty list
        when(fileService.uploadFiles(anyList())).thenReturn(List.of());

        // when
        postService.createPost(dto, files);

        // then
        verify(fileService, times(1)).uploadFiles(files);
        verify(postRepository, times(1)).save(postCaptor.capture());

        Post savedPost = postCaptor.getValue();
        assertEquals("Test Title", savedPost.getTitle());
        assertEquals("Test Content", savedPost.getContent());
        assertEquals("Test Team", savedPost.getTeam());
        assertEquals("https://youtube.com/test", savedPost.getYoutubelink());
        assertTrue(savedPost.getFileIds().isEmpty()); // fileIds가 비어 있는지 확인
    }

    @Test
    void 파일_업로드_예외_발생() throws Exception {
        // given
        PostCreateDto dto = new PostCreateDto();
        dto.setTitle("Test Title");
        dto.setContent("Test Content");
        dto.setTeam("Test Team");
        dto.setYoutubelink("https://www.youtube.com/watch?v=dQw4w9WgXcQ");

        MockMultipartFile file = new MockMultipartFile(
                "files",
                "file.txt",
                "text/plain",
                "File Content".getBytes()
        );

        List<MultipartFile> files = Arrays.asList(file);

        // Mock uploadFiles to throw exception
        when(fileService.uploadFiles(anyList()))
                .thenThrow(new InvalidFileNameException("파일 이름이 유효하지 않습니다."));

        // when & then
        InvalidFileNameException exception = assertThrows(
                InvalidFileNameException.class,
                () -> postService.createPost(dto, files)
        );
        assertEquals("파일 이름이 유효하지 않습니다.", exception.getMessage());

        verify(fileService, times(1)).uploadFiles(files);
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void 단일_게시물_조회_테스트() {
        // given
        Long postId = 1L;
        Post post = Post.builder()
                .postid(postId)
                .title("Test Title")
                .content("Test Content")
                .team("Test Team")
                .youtubelink("https://youtube.com/test")
                .fileIds(Arrays.asList(100L, 101L))
                .build();

        when(postRepository.findById(postId)).thenReturn(java.util.Optional.of(post));

        // when
        Post foundPost = postService.getSinglePost(postId);

        // then
        assertNotNull(foundPost);
        assertEquals(postId, foundPost.getPostid());
        assertEquals("Test Title", foundPost.getTitle());
        assertEquals("Test Content", foundPost.getContent());
        assertEquals("Test Team", foundPost.getTeam());
        assertEquals("https://youtube.com/test", foundPost.getYoutubelink());
        assertEquals(2, foundPost.getFileIds().size());
    }

    @Test
    void 단일_게시물_조회_없는_경우() {
        // given
        Long postId = 1L;
        when(postRepository.findById(postId)).thenReturn(java.util.Optional.empty());

        // when & then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> postService.getSinglePost(postId)
        );

        assertEquals("게시물을 찾을 수 없습니다. 게시물 번호: " + postId, exception.getMessage());
    }
}
