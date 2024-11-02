package jeiu.capstone.jongGangHaejo.controller;

import jakarta.validation.Valid;
import jeiu.capstone.jongGangHaejo.domain.File;
import jeiu.capstone.jongGangHaejo.domain.Post;
import jeiu.capstone.jongGangHaejo.dto.response.controllerAdvice.PostUploadExceptionDto;
import jeiu.capstone.jongGangHaejo.service.FileService;
import jeiu.capstone.jongGangHaejo.service.PostService;
import jeiu.capstone.jongGangHaejo.dto.request.PostCreateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
            @RequestPart("files") List<MultipartFile> files // 첨부 파일들
    ) {
        // 게시물 데이터 로깅
        log.info("게시물 작성 요청 / 제목: {}, 팀: {}", postCreateDto.getTitle(), postCreateDto.getTeam());
        // 각 파일의 이름과 크기 로깅
        files.forEach(file -> log.info("제공된 파일 명: {}, 크기: {} bytes", file.getOriginalFilename(), file.getSize()));
        // 서비스 계층으로 게시물 생성 요청 위임
        postService.createPost(postCreateDto, files);

        // 성공 메시지 반환
        return ResponseEntity.ok(Map.of("message", "게시물이 성공적으로 생성되었습니다."));
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
}
