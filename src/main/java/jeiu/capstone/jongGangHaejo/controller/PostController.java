package jeiu.capstone.jongGangHaejo.controller;

import jakarta.validation.Valid;
import jeiu.capstone.jongGangHaejo.service.PostService;
import jeiu.capstone.jongGangHaejo.dto.request.PostCreateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.List;

@RequiredArgsConstructor // 생성자 주입을 위한 Lombok 어노테이션
@Slf4j // 로깅을 위한 Lombok 어노테이션
@RestController // REST API 컨트롤러
public class PostController {

    private final PostService postService;

    /**
     * 테스트 엔드포인트
     */
    @GetMapping("/test")
    public String getTest() {
        return "안녕하세요 종강해조 프로젝트입니다";
    }

    /**
     * 게시물 생성 엔드포인트
     *
     * @param postCreateDto 게시물 생성 DTO
     * @param files         업로드할 파일 목록
     * @return 응답 메시지
     */
    @PostMapping("/post")
    public ResponseEntity<Map<String, String>> createPost(
            @Valid @RequestPart("post") PostCreateDto postCreateDto, // 게시물 데이터
            @RequestPart("files") List<MultipartFile> files // 첨부 파일들
    ) {
        // 서비스 계층으로 게시물 생성 요청 위임
        postService.createPost(postCreateDto, files);

        // 성공 메시지 반환
        return ResponseEntity.ok(Map.of("message", "게시물이 성공적으로 생성되었습니다."));
    }
}
