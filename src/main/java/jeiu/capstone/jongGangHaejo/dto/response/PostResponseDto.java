package jeiu.capstone.jongGangHaejo.dto.response;

import java.util.ArrayList;
import java.util.List;

import org.apache.tomcat.jni.FileInfo;

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
    private List<FileInfo> files;

    public PostResponseDto(Long id, String title, String content, String team, String youtubelink, String username, String createdAt, String updatedAt, Long viewCount, Long likeCount) {
        this(id, title, content, team, youtubelink, username, createdAt, updatedAt, viewCount, likeCount, false, new ArrayList<>());
    }

    @Getter
    @AllArgsConstructor
    public static class FileInfo {
        private String fileName;
        private String downloadUrl;
        private String thumbnailUrl;
    }
}
