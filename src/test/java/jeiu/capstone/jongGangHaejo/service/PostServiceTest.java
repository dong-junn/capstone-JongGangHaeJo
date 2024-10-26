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
    void createPost_Success() throws Exception {
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
        assertEquals(2, savedPost.getFiles().size());

        for (File file : savedPost.getFiles()) {
            assertNotNull(file.getFileName());
            assertNotNull(file.getS3Path());
            assertNotNull(file.getFileType());
            assertTrue(file.getFileSize() > 0);
        }
    }

    @Test
    void createPost_WithEmptyFile_ShouldSkip() throws Exception {
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
        assertTrue(savedPost.getFiles().isEmpty());
    }

    @Test
    void createPost_FileServiceThrowsException_ShouldPropagate() throws Exception {
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
