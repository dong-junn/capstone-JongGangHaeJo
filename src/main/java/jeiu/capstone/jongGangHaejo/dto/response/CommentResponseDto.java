package jeiu.capstone.jongGangHaejo.dto.response;

import jeiu.capstone.jongGangHaejo.domain.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter
@NoArgsConstructor
public class CommentResponseDto {
    private Long id;           // 댓글 ID
    private String content;    // 댓글 내용
    private String username;   // 작성자
    private String createdAt;  // 생성일시
    private String updatedAt;  // 수정일시

    // Comment 엔티티를 DTO로 변환하는 생성자
    public CommentResponseDto(Comment comment) {
        this.id = comment.getCommentId();
        this.content = comment.getContent();
        this.username = comment.getUsername();
        this.createdAt = comment.getCreatedAt().toString();
        this.updatedAt = comment.getUpdatedAt().toString();
    }
} 