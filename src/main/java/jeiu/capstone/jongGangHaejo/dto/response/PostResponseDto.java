package jeiu.capstone.jongGangHaejo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostResponseDto {
    private Long id;
    private String title;
    private String content;
    private String team;
    private String youtubelink;
    private String username; // 게시물 작성자
    private String createdAt;
    private String updatedAt;
    private Long viewCount;
    private Long likeCount; // 좋아요 수 필드 추가
    private boolean isLiked;  // 추가

    public PostResponseDto(Long id, String title, String content, String team, String youtubelink, String username, String createdAt, String updatedAt, Long viewCount, Long likeCount) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.team = team;
        this.youtubelink = youtubelink;
        this.username = username;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
    }
}
