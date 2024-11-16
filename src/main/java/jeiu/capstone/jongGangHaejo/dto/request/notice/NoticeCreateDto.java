package jeiu.capstone.jongGangHaejo.dto.request.notice;

import jeiu.capstone.jongGangHaejo.domain.Notice;
import lombok.Getter;

@Getter
public class NoticeCreateDto {

    private String title;
    private String content;

    public Notice toEntity() {
        return Notice.builder()
                .title(this.title)
                .content(this.content)
                .build();
    }
}
