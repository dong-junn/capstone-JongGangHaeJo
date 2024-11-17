package jeiu.capstone.jongGangHaejo.dto.notice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import jeiu.capstone.jongGangHaejo.domain.Notice;
import jeiu.capstone.jongGangHaejo.domain.File;
import lombok.*;

// Response DTO
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class NoticeResponse {
    private Long id;
    private String title;
    private String content;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<FileResponse> files;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FileResponse {
        private String fileName;
        private String downloadUrl;
    }

    public static NoticeResponse from(Notice notice, List<File> files) {
        List<FileResponse> fileResponses = files.stream()
                .map(file -> FileResponse.builder()
                        .fileName(file.getFileName())
                        .downloadUrl(file.getS3Path())
                        .build())
                .collect(Collectors.toList());

        return NoticeResponse.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .username(notice.getCreator())
                .createdAt(notice.getCreatedAt())
                .updatedAt(notice.getModifiedAt())
                .files(fileResponses)
                .build();
    }

    public static NoticeResponse from(Notice notice) {
        return NoticeResponse.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .createdAt(notice.getCreatedAt())
                .username(notice.getCreator())
                .build();
    }
}
