package jeiu.capstone.jongGangHaejo.security.dto.admin;

import jeiu.capstone.jongGangHaejo.domain.Post;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AdminPostResponse {

    private final Long id;
    private final String title;
    private final String content;

    // 생성자 오버로딩
    public AdminPostResponse(Post post) {
        this.id = post.getPostid();
        this.title = post.getTitle();
        this.content = post.getContent();
    }

    @Builder
    public AdminPostResponse(Long id, String title, String content) {
        this.id = id;
        this.title = title.substring(0, Math.min(title.length(), 10));
        this.content = content;
    }

}
