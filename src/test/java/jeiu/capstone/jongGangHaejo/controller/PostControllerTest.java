/*
package jeiu.capstone.jongGangHaejo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jeiu.capstone.jongGangHaejo.annotation.WithMockCustomUser;
import jeiu.capstone.jongGangHaejo.config.SecurityTestConfig;
import jeiu.capstone.jongGangHaejo.domain.user.Role;
import jeiu.capstone.jongGangHaejo.domain.user.User;
import jeiu.capstone.jongGangHaejo.dto.request.PostCreateDto;
import jeiu.capstone.jongGangHaejo.dto.request.PostUpdateDto;
import jeiu.capstone.jongGangHaejo.dto.response.PagedResponseDto;
import jeiu.capstone.jongGangHaejo.dto.response.PostResponseDto;
import jeiu.capstone.jongGangHaejo.exception.InvalidFileNameException;
import jeiu.capstone.jongGangHaejo.exception.ResourceNotFoundException;
import jeiu.capstone.jongGangHaejo.exception.UnauthorizedException;
import jeiu.capstone.jongGangHaejo.exception.common.CommonErrorCode;
import jeiu.capstone.jongGangHaejo.security.config.UserConfig;
import jeiu.capstone.jongGangHaejo.security.jwt.JwtUtil;
import jeiu.capstone.jongGangHaejo.service.FileService;
import jeiu.capstone.jongGangHaejo.service.PostService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
@Import(SecurityTestConfig.class)
@WithMockCustomUser
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @MockBean
    private FileService fileService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void 게시물_생성_테스트() throws Exception {
        // given
        PostCreateDto dto = new PostCreateDto();
        dto.setTitle("Test Title");
        dto.setContent("Test Content");
        dto.setTeam("Test Team");
        dto.setYoutubelink("https://www.youtube.com/watch?v=hUhrvhLkSyA");

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

        List<MockMultipartFile> files = List.of(file1, file2);

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
        Mockito.verify(postService, Mockito.times(1)).createPost(any(PostCreateDto.class), any(List.class));
    }

    @Test
    void 게시물_생성_유효하지_않은_파일명() throws Exception {
        // given
        PostCreateDto dto = new PostCreateDto();
        dto.setTitle("Test Title");
        dto.setContent("Test Content");
        dto.setTeam("Test Team");
        dto.setYoutubelink("https://www.youtube.com/watch?v=hUhrvhLkSyA");

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
        Mockito.verify(postService, Mockito.times(1)).createPost(any(PostCreateDto.class), any(List.class));
    }

    @Test
    @WithMockUser
    void 게시물_수정_테스트() throws Exception {
        // given
        Long postId = 1L;
        PostUpdateDto dto = new PostUpdateDto();
        dto.setTitle("Updated Title");
        dto.setContent("Updated Content");
        dto.setTeam("Updated Team");
        dto.setYoutubelink("https://www.youtube.com/watch?v=hUhrvhLkSyA");

        MockMultipartFile postPart = new MockMultipartFile(
                "post",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(dto)
        );

        MockMultipartFile file1 = new MockMultipartFile(
                "files",
                "updatedFile1.txt",
                "text/plain",
                "Updated File 1 Content".getBytes()
        );

        List<MockMultipartFile> files = List.of(file1);

        // `updatePost`는 void이므로 `doNothing()` 사용
        doNothing().when(postService).updatePost(any(Long.class), any(PostUpdateDto.class), any(List.class));

        // when & then
        mockMvc.perform(multipart("/post/" + postId)
                        .file(postPart)
                        .file(file1)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .with(request -> {
                            request.setMethod("PUT"); // multipart 요청에서 PUT 메서드 설정
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("게시물이 성공적으로 수정되었습니다."));

        // verify
        Mockito.verify(postService, Mockito.times(1)).updatePost(any(Long.class), any(PostUpdateDto.class), any(List.class));
    }

    @Test
    @WithMockUser
    void 올바르지_않은_게시물_번호_수정_요청() throws Exception {
        // given
        Long postId = 1L;
        PostUpdateDto dto = new PostUpdateDto();
        dto.setTitle("Updated Title");
        dto.setContent("Updated Content");
        dto.setTeam("Updated Team");
        dto.setYoutubelink("https://www.youtube.com/watch?v=hUhrvhLkSyA");

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

        // `updatePost`가 예외를 던지도록 설정
        doThrow(new ResourceNotFoundException("게시물을 찾을 수 없습니다. 게시물 번호: " + postId, CommonErrorCode.RESOURCE_NOT_FOUND))
                .when(postService).updatePost(any(Long.class), any(PostUpdateDto.class), any(List.class));

        // when & then
        mockMvc.perform(multipart("/post/" + postId)
                        .file(postPart)
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .with(request -> {
                            request.setMethod("PUT"); // multipart 요청에서 PUT 메서드 설정
                            return request;
                        }))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("4041")) // `ResourceNotFoundException`의 code 확인
                .andExpect(jsonPath("$.message").value("게시물을 찾을 수 없습니다. 게시물 번호: " + postId));

        // verify
        Mockito.verify(postService, Mockito.times(1)).updatePost(any(Long.class), any(PostUpdateDto.class), any(List.class));
    }

    @Test
    @WithMockUser
    void 게시물_수정_유효하지_않은_파일명() throws Exception {
        // given
        Long postId = 1L;
        PostUpdateDto dto = new PostUpdateDto();
        dto.setTitle("Updated Title");
        dto.setContent("Updated Content");
        dto.setTeam("Updated Team");
        dto.setYoutubelink("https://www.youtube.com/watch?v=hUhrvhLkSyA");

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

        // `updatePost`가 예외를 던지도록 설정
        doThrow(new InvalidFileNameException("파일 이름이 유효하지 않습니다."))
                .when(postService).updatePost(any(Long.class), any(PostUpdateDto.class), any(List.class));

        // when & then
        mockMvc.perform(multipart("/post/" + postId)
                        .file(postPart)
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .with(request -> {
                            request.setMethod("PUT"); // multipart 요청에서 PUT 메서드 설정
                            return request;
                        }))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("4001")) // `CommonErrorCode.INVALID_ARGUMENT_ERROR`의 code
                .andExpect(jsonPath("$.message").value("올바르지 않은 파라미터입니다."));

        // verify
        Mockito.verify(postService, Mockito.times(1)).updatePost(any(Long.class), any(PostUpdateDto.class), any(List.class));
    }

    @Test
    @WithMockUser(username = "anotherUser")
    void 게시물_수정_올바르지_않은_사용자_요청() throws Exception {
        // given
        Long postId = 1L;
        PostUpdateDto dto = new PostUpdateDto();
        dto.setTitle("Updated Title");
        dto.setContent("Updated Content");
        dto.setTeam("Updated Team");
        dto.setYoutubelink("https://www.youtube.com/watch?v=hUhrvhLkSyA");

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

        // `updatePost`가 예외를 던지도록 설정
        doThrow(new UnauthorizedException("인증되지 않았거나 권한이 없습니다.", CommonErrorCode.UNAUTHORIZED_ERROR))
                .when(postService).updatePost(any(Long.class), any(PostUpdateDto.class), any(List.class));

        // when & then
        mockMvc.perform(multipart("/post/" + postId)
                        .file(postPart)
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .with(request -> {
                            request.setMethod("PUT"); // multipart 요청에서 PUT 메서드 설정
                            return request;
                        }))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("4010"))
                .andExpect(jsonPath("$.message").value("인증되지 않았거나 권한이 없습니다."));

        // verify
        Mockito.verify(postService, Mockito.times(1)).updatePost(any(Long.class), any(PostUpdateDto.class), any(List.class));
    }

    @Test
    @WithMockUser(username = "user")
    void 게시물_삭제_테스트() throws Exception {
        // given
        Long postId = 1L;

        // `deletePost`는 void이므로 `doNothing()` 사용
        doNothing().when(postService).deletePost(postId);

        // when & then
        mockMvc.perform(delete("/post/" + postId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("게시물이 성공적으로 삭제되었습니다."));

        // verify
        Mockito.verify(postService, Mockito.times(1)).deletePost(postId);
    }

    */
/**
     * 존재하지 않는 게시물 삭제 시 예외 테스트
     *//*

    @Test
    @WithMockUser(username = "user")
    void 유효하지_않은_게시물_삭제_요청() throws Exception {
        // given
        Long postId = 1L;

        // `deletePost`가 예외를 던지도록 설정
        doThrow(new ResourceNotFoundException("게시물을 찾을 수 없습니다. 게시물 ID: " + postId, CommonErrorCode.RESOURCE_NOT_FOUND))
                .when(postService).deletePost(postId);

        // when & then
        mockMvc.perform(delete("/post/" + postId)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("4041")) // `CommonErrorCode.RESOURCE_NOT_FOUND`의 code
                .andExpect(jsonPath("$.message").value("게시물을 찾을 수 없습니다. 게시물 ID: " + postId));

        // verify
        Mockito.verify(postService, Mockito.times(1)).deletePost(postId);
    }

    */
/**
     * 권한이 없는 사용자의 게시물 삭제 시 예외 테스트
     *//*

    @Test
    @WithMockUser(username = "anotherUser")
    void 작성자가_아닌_게시물_삭제_요청() throws Exception {
        // given
        Long postId = 1L;

        // `deletePost`가 예외를 던지도록 설정
        doThrow(new UnauthorizedException("게시물을 삭제할 권한이 없습니다.", CommonErrorCode.UNAUTHORIZED_ERROR))
                .when(postService).deletePost(postId);

        // when & then
        mockMvc.perform(delete("/post/" + postId)
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("4010")) // `CommonErrorCode.UNAUTHORIZED_ERROR`의 code
                .andExpect(jsonPath("$.message").value("게시물을 삭제할 권한이 없습니다."));

        // verify
        Mockito.verify(postService, Mockito.times(1)).deletePost(postId);
    }

    */
/**
     * 페이징된 게시물 목록 조회 성공 테스트
     *//*

    @Test
    @WithMockUser
    void 페이징_처리_게시물_불러오기() throws Exception {
        // given
        int page = 0;
        int size = 10;
        String sortBy = "createdAt"; // 작성일 기준
        String direction = "desc"; // 역순 (최근 날짜부터)

        PostResponseDto post1 = new PostResponseDto(1L, "Title1", "Content1", "Team1", "https://youtube.com/1", "user1", "2024-01-01", "2024-01-02", 0L);
        PostResponseDto post2 = new PostResponseDto(2L, "Title2", "Content2", "Team2", "https://youtube.com/2", "user2", "2024-01-03", "2024-01-04", 0L);

        List<PostResponseDto> posts = List.of(post1, post2);
        PagedResponseDto<PostResponseDto> pagedResponseDto = new PagedResponseDto<>(posts, page, size, 2, 1, true);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sortBy));

        when(postService.getPagedPosts(any(Pageable.class))).thenReturn(pagedResponseDto);

        // when & then
        mockMvc.perform(get("/posts")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("sort", sortBy + "," + direction)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(post1.getId()))
                .andExpect(jsonPath("$.content[0].title").value(post1.getTitle()))
                .andExpect(jsonPath("$.content[1].id").value(post2.getId()))
                .andExpect(jsonPath("$.content[1].title").value(post2.getTitle()))
                .andExpect(jsonPath("$.page").value(page))
                .andExpect(jsonPath("$.size").value(size))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.last").value(true));

        // verify
        Mockito.verify(postService, Mockito.times(1)).getPagedPosts(any(Pageable.class));
    }

    */
/**
     * 페이징된 게시물 목록 조회 시 잘못된 페이지 요청 테스트
     *//*

    @Test
    @WithMockUser
    void 잘못된_페이지_번호_요청() throws Exception {
        // given
        String invalidPage = "invalid";
        int size = 10;
        String sortBy = "createdAt";
        String direction = "desc";

        // when & then
        mockMvc.perform(get("/posts")
                        .param("page", invalidPage)
                        .param("size", String.valueOf(size))
                        .param("sort", sortBy + "," + direction)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(CommonErrorCode.INVALID_ARGUMENT_ERROR.getCode()))
                .andExpect(jsonPath("$.message").value(Matchers.containsString("파라미터의 타입이 잘못되었습니다")));

        // verify
        Mockito.verify(postService, Mockito.times(0)).getPagedPosts(any(Pageable.class));
    }

}
 */
