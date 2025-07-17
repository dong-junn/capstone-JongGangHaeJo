package jeiu.capstone.jongGangHaejo.presentation;

import jakarta.validation.Valid;
import jeiu.capstone.jongGangHaejo.dto.request.PostUpdateDto;
import jeiu.capstone.jongGangHaejo.dto.response.PagedResponseDto;
import jeiu.capstone.jongGangHaejo.dto.response.PostResponseDto;
import jeiu.capstone.jongGangHaejo.security.config.UserConfig;
import jeiu.capstone.jongGangHaejo.service.FileService;
import jeiu.capstone.jongGangHaejo.service.PostService;
import jeiu.capstone.jongGangHaejo.dto.request.PostCreateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Map;
import java.util.List;

@RequiredArgsConstructor // 생성자 주입을 위한 Lombok 어노테이션
@Slf4j // 로깅을 위한 Lombok 어노테이션
@RestController // REST API 컨트롤러
public class PostController {

    private final PostService postService;
    private final FileService fileService;

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
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
            @AuthenticationPrincipal UserConfig userConfig // 현재 인증된 사용자 정보
    ) {
        // 게시물 데이터 로깅
        log.info("게시물 작성 요청 / 제목: {}, 팀: {}", postCreateDto.getTitle(), postCreateDto.getTeam());
        
        // 파일 로깅 (null 체크 추가)
        if (files != null) {
            files.forEach(file -> log.info("제공된 파일 명: {}, 크기: {} bytes", file.getOriginalFilename(), file.getSize()));
        }
        
        if (thumbnail != null) {
            log.info("제공된 썸네일 파일 명: {}, 크기: {} bytes", thumbnail.getOriginalFilename(), thumbnail.getSize());
        }

        // 사용자 이름 설정
        postCreateDto.setUsername(userConfig.getUsername());

        // 서비스 계층으로 게시물 생성 요청 위임
        postService.createPost(postCreateDto, files != null ? files : Collections.emptyList(), thumbnail);

        // 성공 메시지 반환
        return ResponseEntity.ok(Map.of("message", "게시물이 성공적으로 생성되었습니다."));
    }

    @PutMapping("/post/{postId}")
    public ResponseEntity<Map<String, String>> updatePost(
            @PathVariable Long postId,
            @Validated @RequestPart("post") PostUpdateDto postUpdateDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail
    ) {
        log.info("게시물 수정 요청 / 게시물 ID: {}, 제목: {}, 팀: {}", postId, postUpdateDto.getTitle(), postUpdateDto.getTeam());
        if (files != null) {
            files.forEach(file -> log.info("제공된 파일 명: {}, 크기: {} bytes", file.getOriginalFilename(), file.getSize()));
        }
        if (thumbnail != null) {
            log.info("제공된 썸네일 파일 명: {}, 크기: {} bytes", thumbnail.getOriginalFilename(), thumbnail.getSize());
        }
        
        postService.updatePost(postId, postUpdateDto, files, thumbnail);
        return ResponseEntity.ok(Map.of("message", "게시물이 성공적으로 수정되었습니다."));
    }

    @GetMapping("/post/{id}")
    public ResponseEntity<PostResponseDto> getPost(@PathVariable Long id) {
        PostResponseDto post = postService.getSinglePost(id);
        return ResponseEntity.ok(post);
    }

    /**
     * 페이징된 게시물 목록 조회 엔드포인트
     *
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @param sort 정렬 기준 (예: createdAt,desc)
     * @return 페이징된 게시물 응답 DTO
     */
    @GetMapping("/post")
    public ResponseEntity<PagedResponseDto<PostResponseDto>> getPagedPosts(
            @RequestParam(defaultValue = "1") int page,  // 1부터 시작하는 페이지
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort
    ) {
//        log.info("페이징된 게시물 조회 요청 / 페이지: {}, 크기: {}, 정렬: {}", page, size, sort);

        int pageNumber = page - 1;
        // 정렬 설정
        Sort.Direction direction = Sort.Direction.DESC;
        String sortBy = "createdAt"; // 기본 정렬 기준 현재는 작성일

        if (sort.length == 2) {
            sortBy = sort[0];
            direction = Sort.Direction.fromString(sort[1]);
        } else if (sort.length == 1) {
            sortBy = sort[0];
        }

        Pageable pageable = PageRequest.of(pageNumber, size, Sort.by(direction, sortBy));

        PagedResponseDto<PostResponseDto> pagedPosts = postService.getPagedPosts(pageable);

        return ResponseEntity.ok(new PagedResponseDto<>(
        pagedPosts.getContent(),
        pagedPosts.getPage() + 1,  // 0부터 시작하는 페이지를 1부터 시작하도록 변환
        pagedPosts.getSize(),
        pagedPosts.getTotalElements(),
        pagedPosts.getTotalPages(),
        pagedPosts.isLast()
    ));
    }

    @DeleteMapping("/post/{postId}")
    public ResponseEntity<Map<String, String>> deletePost(
            @PathVariable Long postId
    ) {
        log.info("게시물 삭제 요청 / 게시물 ID : {}", postId);
        postService.deletePost(postId);
        return ResponseEntity.ok(Map.of("message", "게시물이 성공적으로 삭제되었습니다."));

    }
}
