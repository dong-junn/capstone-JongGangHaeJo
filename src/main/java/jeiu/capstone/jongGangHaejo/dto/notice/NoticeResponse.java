package jeiu.capstone.jongGangHaejo.dto.notice;

import jeiu.capstone.jongGangHaejo.domain.Notice;
import jeiu.capstone.jongGangHaejo.domain.user.User;
import jeiu.capstone.jongGangHaejo.dto.admin.user.UserResponse;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// Response DTO
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeResponse {
    private Long id;
    private String title;
    private String content;
    private String creator;  // user 대신 creator 사용

    @Builder
    public NoticeResponse(Long id, String title, String content, String creator) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.creator = creator;
    }

    public static NoticeResponse from(Notice notice) {
        return NoticeResponse.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .creator(notice.getCreator())
                .build();
    }
}
