package jeiu.capstone.jongGangHaejo.dto.response;

import jeiu.capstone.jongGangHaejo.domain.File;
import jeiu.capstone.jongGangHaejo.domain.Post;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class PostDetailResponseDto {

    private Long postid;
    private String title;
    private String content;
    private String username;
    private String team;
    private String youtubelink;
    private List<FileDto> files;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 파일 정보를 위한 내부 클래스
    @Getter
    public static class FileDto {
        private String fileName;
        private String s3Path;
        private String fileType;
        private Long fileSize;

        public static FileDto from(File file) {
            FileDto dto = new FileDto();
            dto.fileName = file.getFileName();
            dto.s3Path = file.getS3Path();
            dto.fileType = file.getFileType();
            dto.fileSize = file.getFileSize();
            return dto;
        }
    }

    // 엔티티를 DTO로 변환하는 정적 메서드
    public static PostDetailResponseDto from(Post post) {
        PostDetailResponseDto dto = new PostDetailResponseDto();
        dto.postid = post.getPostid();
        dto.title = post.getTitle();
        dto.content = post.getContent();
        dto.username = post.getUsername();
        dto.team = post.getTeam();
        dto.youtubelink = post.getYoutubelink();
        dto.files = post.getFiles().stream()
                .map(FileDto::from)
                .collect(Collectors.toList());
        dto.createdAt = post.getCreatedAt();
        dto.updatedAt = post.getUpdatedAt();
        return dto;
    }
}
