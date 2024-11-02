package jeiu.capstone.jongGangHaejo.controller.exception;

import jeiu.capstone.jongGangHaejo.controller.PostController;
import jeiu.capstone.jongGangHaejo.dto.request.PostCreateDto;
import jeiu.capstone.jongGangHaejo.exception.InvalidFileNameException;
import jeiu.capstone.jongGangHaejo.service.FileService;
import jeiu.capstone.jongGangHaejo.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
class ExceptionHandlingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @MockBean
    private FileService fileService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createPost_Success() throws Exception {
        // given
        PostCreateDto dto = new PostCreateDto();
        dto.setTitle("Test Title");
        dto.setContent("Test Content");
        dto.setTeam("Test Team");
        dto.setYoutubelink("https://www.youtube.com/watch?v=dQw4w9WgXcQ");

        MockMultipartFile postPart = new MockMultipartFile(
                "post",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(dto)
        );

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

        // `createPost`는 void이므로 `doNothing()` 사용
        doNothing().when(postService).createPost(any(PostCreateDto.class), any(List.class));

        // when & then
        mockMvc.perform(multipart("/post")
                        .file(postPart)
                        .file(file1)
                        .file(file2)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("게시물이 성공적으로 생성되었습니다."));

        // verify
        // PostService.createPost가 정확히 한 번 호출되었는지 검증
        org.mockito.Mockito.verify(postService, org.mockito.Mockito.times(1)).createPost(any(PostCreateDto.class), any(List.class));
    }

    @Test
    void createPost_FileServiceThrowsException_ShouldReturnErrorResponse() throws Exception {
        // given
        PostCreateDto dto = new PostCreateDto();
        dto.setTitle("Test Title");
        dto.setContent("Test Content");
        dto.setTeam("Test Team");
        dto.setYoutubelink("https://www.youtube.com/watch?v=dQw4w9WgXcQ");

        MockMultipartFile postPart = new MockMultipartFile(
                "post",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(dto)
        );

        MockMultipartFile file = new MockMultipartFile(
                "files",
                "file.txt",
                "text/plain",
                "File Content".getBytes()
        );

        List<MockMultipartFile> files = List.of(file);

        // `createPost`가 예외를 던지도록 설정 (void 메서드이므로 `doThrow()` 사용)
        doThrow(new InvalidFileNameException("파일 이름이 유효하지 않습니다."))
                .when(postService).createPost(any(PostCreateDto.class), any(List.class));

        // when & then
        mockMvc.perform(multipart("/post")
                        .file(postPart)
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("4001"))
                .andExpect(jsonPath("$.message").value("올바르지 않은 파라미터입니다."));

        // verify
        org.mockito.Mockito.verify(postService, org.mockito.Mockito.times(1)).createPost(any(PostCreateDto.class), any(List.class));
    }
}
