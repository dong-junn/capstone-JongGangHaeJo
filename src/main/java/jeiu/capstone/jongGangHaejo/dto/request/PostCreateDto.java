package jeiu.capstone.jongGangHaejo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jeiu.capstone.jongGangHaejo.domain.Post;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import jeiu.capstone.jongGangHaejo.validation.ValidYoutubeUrl;

@Getter @Setter //안열어 주면 Controller params에 null값이 들어감 -> params=PostCreate(title=null, content=null) [log결과]
@ToString //toString 구현
public class PostCreateDto { //PostController에 params를 넘기기 위한 DTO

    //public으로 노출 할 이유가 없으므로 private
    @NotBlank(message = "제목이 비었습니다. 게시글 제목은 필수로 입력해야 합니다") // null, "", " "를 허용하지 않는다
    private String title;

    @NotBlank(message = "내용이 비었습니다. 게시글 내용은 필수로 입력해야 합니다")
    private String content;

    @NotBlank(message = "팀명이 비었습니다. 팀원은 필수로 입력해야 합니다.")
    private String team;

    @ValidYoutubeUrl //유튜브 링크임을 검증하는 어노테이션
    private String youtubelink;

    private String username;

    /**
     * DTO를 엔티티로 변환하는 메서드
     *
     * @return Post 엔티티
     */
    public Post toEntity() {
        return Post.builder()
                .title(this.title)
                .content(this.content)
                .team(this.team)
                .youtubelink(this.youtubelink)
                .username(this.username)
                .build();
    }
}
