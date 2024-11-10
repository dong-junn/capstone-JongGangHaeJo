package jeiu.capstone.jongGangHaejo.controller;

import jeiu.capstone.jongGangHaejo.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}/like")
public class LikeController {

    private final LikeService likeService;

    @PutMapping
    public ResponseEntity<Map<String, String>> toggleLike(@PathVariable Long postId) {
        log.info("좋아요 토글 요청 / 게시물 번호: {}", postId);
        likeService.toggleLike(postId);
        return ResponseEntity.ok(Map.of("message", "좋아요 상태가 변경되었습니다."));
    }
} 