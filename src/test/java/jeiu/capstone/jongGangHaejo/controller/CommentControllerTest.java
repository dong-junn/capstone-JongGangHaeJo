package jeiu.capstone.jongGangHaejo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jeiu.capstone.jongGangHaejo.dto.request.CommentCreateDto;
import jeiu.capstone.jongGangHaejo.dto.response.CommentResponseDto;
import jeiu.capstone.jongGangHaejo.exception.ResourceNotFoundException;
import jeiu.capstone.jongGangHaejo.exception.UnauthorizedException;
import jeiu.capstone.jongGangHaejo.exception.common.CommonErrorCode;
import jeiu.capstone.jongGangHaejo.service.CommentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest                // 통합 테스트를 위한 어노테이션
@AutoConfigureMockMvc         // MockMvc 자동 설정
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;   // API 요청/응답을 테스트하기 위한 객체

    @MockBean                  // 서비스 계층을 Mock으로 대체
    private CommentService commentService;

    private final ObjectMapper objectMapper = new ObjectMapper();  // JSON 변환용

    @Test
    @WithMockUser             // 인증된 사용자로 테스트
    @DisplayName("댓글 작성 성공")
    void 댓글_작성_성공() throws Exception {
        // given
        Long postId = 1L;
        CommentCreateDto createDto = new CommentCreateDto();
        createDto.setContent("테스트 댓글");

        CommentResponseDto responseDto = new CommentResponseDto();
        responseDto.setId(1L);
        responseDto.setContent("테스트 댓글");
        responseDto.setUsername("user");
        responseDto.setCreatedAt(LocalDateTime.now().toString());
        responseDto.setUpdatedAt(LocalDateTime.now().toString());

        // Mock 서비스 동작 설정
        given(commentService.createComment(eq(postId), any(CommentCreateDto.class)))
                .willReturn(responseDto);

        // when & then
        mockMvc.perform(post("/posts/{postId}/comments", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.content").value("테스트 댓글"))
                .andExpect(jsonPath("$.username").value("user"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    @WithMockUser // 인증된 사용자로 테스트
    @DisplayName("댓글 목록 조회")
    void 댓글_목록_조회() throws Exception {
        // given
        Long postId = 1L;
        CommentResponseDto comment1 = new CommentResponseDto();
        comment1.setId(1L);
        comment1.setContent("첫번째 댓글");
        comment1.setUsername("user1");
        comment1.setCreatedAt(LocalDateTime.now().toString());
        comment1.setUpdatedAt(LocalDateTime.now().toString());

        CommentResponseDto comment2 = new CommentResponseDto();
        comment2.setId(2L);
        comment2.setContent("두번째 댓글");
        comment2.setUsername("user2");
        comment2.setCreatedAt(LocalDateTime.now().toString());
        comment2.setUpdatedAt(LocalDateTime.now().toString());

        given(commentService.getComments(postId)).willReturn(Arrays.asList(comment1, comment2));

        // when & then
        mockMvc.perform(get("/posts/{postId}/comments", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("첫번째 댓글"))
                .andExpect(jsonPath("$[1].content").value("두번째 댓글"));
    }

    @Test
    @WithMockUser // 인증된 사용자로 테스트
    @DisplayName("댓글 삭제 성공")
    void 댓글_삭제_성공() throws Exception {
        // given
        Long postId = 1L;
        Long commentId = 1L;

        // when & then
        mockMvc.perform(delete("/posts/{postId}/comments/{commentId}", postId, commentId)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser // 인증된 사용자로 테스트
    @DisplayName("존재하지 않는 게시글에 댓글 작성 실패")
    void 존재하지_않는_게시글_댓글_작성_실패() throws Exception {
        // given
        Long postId = 999L;
        CommentCreateDto createDto = new CommentCreateDto();
        createDto.setContent("테스트 댓글");

        given(commentService.createComment(eq(postId), any(CommentCreateDto.class)))
                .willThrow(new ResourceNotFoundException("게시물을 찾을 수 없습니다.", CommonErrorCode.RESOURCE_NOT_FOUND));

        // when & then
        mockMvc.perform(post("/posts/{postId}/comments", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(CommonErrorCode.RESOURCE_NOT_FOUND.getCode()));
    }

    @Test
    @WithMockUser // 인증된 사용자로 테스트
    @DisplayName("타인의 댓글 삭제 시도 실패")
    void 타인의_댓글_삭제_시도_실패() throws Exception {
        // given
        Long postId = 1L;
        Long commentId = 1L;

        doThrow(new UnauthorizedException("댓글을 삭제할 권한이 없습니다.", CommonErrorCode.AUTHORIZATION_FAILED))
                .when(commentService).deleteComment(commentId);

        // when & then
        mockMvc.perform(delete("/posts/{postId}/comments/{commentId}", postId, commentId)
                        .with(csrf()))
                .andExpect(status().isForbidden()); // 권한 없음 상태 코드
    }
} 