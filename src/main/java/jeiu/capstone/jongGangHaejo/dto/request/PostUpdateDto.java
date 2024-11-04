package jeiu.capstone.jongGangHaejo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import jeiu.capstone.jongGangHaejo.validation.ValidYoutubeUrl;

@Getter
@Setter
public class PostUpdateDto {

    @NotBlank(message = "제목이 비었습니다. 게시글 제목은 필수로 입력해야 합니다")
    private String title;

    @NotBlank(message = "내용이 비었습니다. 게시글 내용은 필수로 입력해야 합니다")
    private String content;

    @NotBlank(message = "팀명이 비었습니다. 팀원은 필수로 입력해야 합니다.")
    private String team;

    @ValidYoutubeUrl
    private String youtubelink;

    // 추가로 필요한 필드가 있다면 여기에 추가
}
