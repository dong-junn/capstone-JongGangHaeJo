package jeiu.capstone.jongGangHaejo.presentation;

import jeiu.capstone.jongGangHaejo.service.PostService;
import jeiu.capstone.jongGangHaejo.dto.response.PostResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class IndexController {

    private final PostService postService;

    public IndexController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/")
    public List<PostResponseDto> getTopPosts() {
        // 대표 게시물 3개를 가져오는 로직
        return postService.getTop3Posts();
    }
}
