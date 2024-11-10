package jeiu.capstone.jongGangHaejo.service;

import jeiu.capstone.jongGangHaejo.domain.Comment;
import jeiu.capstone.jongGangHaejo.dto.request.CommentCreateDto;
import jeiu.capstone.jongGangHaejo.dto.response.CommentResponseDto;
import jeiu.capstone.jongGangHaejo.exception.ResourceNotFoundException;
import jeiu.capstone.jongGangHaejo.exception.UnauthorizedException;
import jeiu.capstone.jongGangHaejo.exception.common.CommonErrorCode;
import jeiu.capstone.jongGangHaejo.repository.CommentRepository;
import jeiu.capstone.jongGangHaejo.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 댓글 관련 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    /**
     * 댓글을 생성합니다.
     * @param postId 게시글 ID
     * @param dto 댓글 생성 DTO
     * @return 생성된 댓글 응답 DTO
     */
    @Transactional
    public CommentResponseDto createComment(Long postId, CommentCreateDto dto) {
        // 게시글 존재 여부 확인
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException(
                "게시물을 찾을 수 없습니다. 게시물 번호: " + postId, 
                CommonErrorCode.RESOURCE_NOT_FOUND
            );
        }

        // 현재 로그인한 사용자 정보 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // 댓글 엔티티 생성 및 저장
        Comment comment = Comment.builder()
                .content(dto.getContent())
                .username(username)
                .postId(postId)
                .parentCommentId(dto.getParentCommentId()) // 부모 댓글 ID
                .build();

        Comment savedComment = commentRepository.save(comment);
        return new CommentResponseDto(savedComment);
    }

    /**
     * 특정 게시글의 댓글 목록을 조회합니다.
     * @param postId 게시글 ID
     * @return 댓글 목록
     */
    @Transactional(readOnly = true)
    public List<CommentResponseDto> getComments(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId)
                .stream()
                .map(CommentResponseDto::new)
                .collect(Collectors.toList());
    }

    /**
     * 댓글을 삭제합니다.
     * @param commentId 삭제할 댓글 ID
     */
    @Transactional
    public void deleteComment(Long commentId) {
        // 댓글 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "댓글을 찾을 수 없습니다. 댓글 번호: " + commentId,
                    CommonErrorCode.RESOURCE_NOT_FOUND
                ));

        // 현재 사용자가 작성자인지 확인
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!comment.getUsername().equals(currentUsername)) {
            throw new UnauthorizedException(CommonErrorCode.AUTHORIZATION_FAILED.getMessage(), CommonErrorCode.AUTHORIZATION_FAILED);
        }

        commentRepository.delete(comment);
    }
} 