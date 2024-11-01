package jeiu.capstone.jongGangHaejo.service;

import jeiu.capstone.jongGangHaejo.domain.File;
import jeiu.capstone.jongGangHaejo.domain.Post;
import jeiu.capstone.jongGangHaejo.dto.request.PostCreateDto;
import jeiu.capstone.jongGangHaejo.exception.InvalidFileNameException;
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

        List<MockMultipartFile> files = Arrays.asList(file1, file2);

        when(fileService.uploadFile(any(MockMultipartFile.class)))
                .thenReturn("https://s3.ap-northeast-2.amazonaws.com/test-bucket/" + UUID.randomUUID().toString() + "_file1.txt",
                        "https://s3.ap-northeast-2.amazonaws.com/test-bucket/" + UUID.randomUUID().toString() + "_file2.txt");

        // Mock 설정: saveFile 호출 시 File 객체 반환
        when(fileService.saveFile(any(File.class))).thenAnswer(invocation -> {
            File file = invocation.getArgument(0);
            file.setFileId(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE); // 고유 ID 설정
            return file;
        });

        // when
        postService.createPost(dto, (List<MultipartFile>) (List<?>) files);

        // then
        verify(fileService, times(2)).uploadFile(any(MockMultipartFile.class));
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

        List<MockMultipartFile> files = Arrays.asList(emptyFile);

        // when
        postService.createPost(dto, (List<MultipartFile>) (List<?>) files);

        // then
        verify(fileService, never()).uploadFile(any(MockMultipartFile.class));
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

        List<MockMultipartFile> files = Arrays.asList(file);

        when(fileService.uploadFile(any(MockMultipartFile.class)))
                .thenThrow(new InvalidFileNameException("Invalid file name"));

        // when & then
        InvalidFileNameException exception = assertThrows(
                InvalidFileNameException.class,
                () -> postService.createPost(dto, (List<MultipartFile>) (List<?>) files)
        );
        assertEquals("Invalid file name", exception.getMessage());

        verify(fileService, times(1)).uploadFile(any(MockMultipartFile.class));
        verify(postRepository, never()).save(any(Post.class));
    }
}
