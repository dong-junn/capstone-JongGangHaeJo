package jeiu.capstone.jongGangHaejo.controller;

import jeiu.capstone.jongGangHaejo.dto.request.CommentCreateDto;
import jeiu.capstone.jongGangHaejo.dto.response.CommentResponseDto;
import jeiu.capstone.jongGangHaejo.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 댓글 관련 요청을 처리하는 컨트롤러 클래스
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글을 생성합니다.
     * @param postId 게시글 ID
     * @param dto 댓글 생성 DTO
     * @return 생성된 댓글 응답 DTO
     */
    @PostMapping
    public ResponseEntity<CommentResponseDto> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentCreateDto dto) {
        log.info("댓글 작성 요청 / 게시글 번호: {}, 내용: {}", postId, dto.getContent());
        return ResponseEntity.ok(commentService.createComment(postId, dto));
    }

    /**
     * 특정 게시글의 댓글 목록을 조회합니다.
     * @param postId 게시글 ID
     * @return 댓글 목록
     */
    @GetMapping
    public ResponseEntity<List<CommentResponseDto>> getComments(@PathVariable Long postId) {
        log.info("댓글 목록 조회 요청 / 게시글 번호: {}", postId);
        return ResponseEntity.ok(commentService.getComments(postId));
    }

    /**
     * 댓글을 삭제합니다.
     * @param postId 게시글 ID
     * @param commentId 댓글 ID
     * @return 삭제 성공 메시지
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Map<String, String>> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId) {
        log.info("댓글 삭제 요청 / 게시글 번호: {}, 댓글 번호: {}", postId, commentId);
        commentService.deleteComment(commentId);
        return ResponseEntity.ok(Map.of("message", "댓글이 성공적으로 삭제되었습니다."));
    }
} 