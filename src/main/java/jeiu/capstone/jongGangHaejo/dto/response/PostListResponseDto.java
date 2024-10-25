package jeiu.capstone.jongGangHaejo.dto.response;

import jeiu.capstone.jongGangHaejo.domain.Post;

import java.time.LocalDateTime;

public class PostListResponseDto {

    private Long postid;
    private String title;
    private String username;
    private String team;
    private LocalDateTime createdAt;

    // 엔티티를 DTO로 변환하는 정적 메서드
    public static PostListResponseDto from(Post post) {
        PostListResponseDto dto = new PostListResponseDto();
        dto.postid = post.getPostid();
        dto.title = post.getTitle();
        dto.username = post.getUsername();
        dto.team = post.getTeam();
        dto.createdAt = post.getCreatedAt();
        return dto;
    }
}
