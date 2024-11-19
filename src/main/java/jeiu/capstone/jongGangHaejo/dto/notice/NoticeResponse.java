package jeiu.capstone.jongGangHaejo.dto.notice;

import java.time.LocalDateTime;

import jeiu.capstone.jongGangHaejo.domain.Notice;
import lombok.*;

// Response DTO
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeResponse {
    private Long id;
    private String title;
    private String content;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static NoticeResponse from(Notice notice) {
        return NoticeResponse.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .username(notice.getCreator())
                .createdAt(notice.getCreatedAt())
                .updatedAt(notice.getModifiedAt())
                .build();
    }
}
