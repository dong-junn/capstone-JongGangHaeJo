package jeiu.capstone.jongGangHaejo.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostResponseDto {
    private Long id;
    private String title;
    private String content;
    private String team;
    private String youtubelink;
    private String username; // 게시물 작성자
    private String createdAt;
    private String updatedAt;

    // 필요에 따라 파일 정보도 포함할 수 있습니다.

    public PostResponseDto(Long id, String title, String content, String team, String youtubelink, String username, String createdAt, String updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.team = team;
        this.youtubelink = youtubelink;
        this.username = username;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
