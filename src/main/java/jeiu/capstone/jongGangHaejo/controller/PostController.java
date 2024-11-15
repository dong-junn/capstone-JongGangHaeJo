package jeiu.capstone.jongGangHaejo.controller;

import jakarta.validation.Valid;
import jeiu.capstone.jongGangHaejo.domain.File;
import jeiu.capstone.jongGangHaejo.domain.Post;
import jeiu.capstone.jongGangHaejo.dto.request.PostUpdateDto;
import jeiu.capstone.jongGangHaejo.dto.response.PagedResponseDto;
import jeiu.capstone.jongGangHaejo.dto.response.PostResponseDto;
import jeiu.capstone.jongGangHaejo.dto.response.controllerAdvice.PostUploadExceptionDto;
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

import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

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
            @RequestPart("files") List<MultipartFile> files,
            @AuthenticationPrincipal UserConfig userConfig // 현재 인증된 사용자 정보
    ) {
        // 게시물 데이터 로깅
        log.info("게시물 작성 요청 / 제목: {}, 팀: {}", postCreateDto.getTitle(), postCreateDto.getTeam());
        // 각 파일의 이름과 크기 로깅
        files.forEach(file -> log.info("제공된 파일 명: {}, 크기: {} bytes", file.getOriginalFilename(), file.getSize()));

        // 사용자 이름 설정
        postCreateDto.setUsername(userConfig.getUsername());

        // 서비스 계층으로 게시물 생성 요청 위임
        postService.createPost(postCreateDto, files);

        // 성공 메시지 반환
        return ResponseEntity.ok(Map.of("message", "게시물이 성공적으로 생성되었습니다."));
    }

    @PutMapping("/post/{postId}")
    public ResponseEntity<Map<String, String>> updatePost(
            @PathVariable Long postId,
            @Validated @RequestPart("post") PostUpdateDto postUpdateDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        log.info("게시물 수정 요청 / 게시물 ID: {}, 제목: {}, 팀: {}", postId, postUpdateDto.getTitle(), postUpdateDto.getTeam());
        if (files != null) {
            files.forEach(file -> log.info("제공된 파일 명: {}, 크기: {} bytes", file.getOriginalFilename(), file.getSize()));
        }
        postService.updatePost(postId, postUpdateDto, files);

        return ResponseEntity.ok(Map.of("message", "게시물이 성공적으로 수정되었습니다."));
    }

    @GetMapping("/post/{postId}")
    public PostUploadExceptionDto getPost(@PathVariable(name = "postId") Long id) {
        Post post = postService.getSinglePost(id); //게시물 불러오기
        List<File> files = fileService.getFilesByIds(post.getFileIds()); // 파일이 여러 개인 경우를 고려해서 리스트로 가져옴

        //게시물 정보 설정
        PostUploadExceptionDto dto = new PostUploadExceptionDto();
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setTeam(post.getTeam());
        dto.setYoutubelink(post.getYoutubelink());

        //첨부파일 설정
        List<PostUploadExceptionDto.FileDTO> fileDTOList = files.stream()
                .map(file -> new PostUploadExceptionDto.FileDTO(file.getS3Path(), file.getFileName()))
                .collect(Collectors.toList());
        dto.setFiles(fileDTOList);

        return dto;
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
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort
    ) {
//        log.info("페이징된 게시물 조회 요청 / 페이지: {}, 크기: {}, 정렬: {}", page, size, sort);

        // 정렬 설정
        Sort.Direction direction = Sort.Direction.DESC;
        String sortBy = "createdAt"; // 기본 정렬 기준 현재는 작성일

        if (sort.length == 2) {
            sortBy = sort[0];
            direction = Sort.Direction.fromString(sort[1]);
        } else if (sort.length == 1) {
            sortBy = sort[0];
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        PagedResponseDto<PostResponseDto> pagedPosts = postService.getPagedPosts(pageable);

        return ResponseEntity.ok(pagedPosts);
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
