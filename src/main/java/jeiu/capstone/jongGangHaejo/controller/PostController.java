package jeiu.capstone.jongGangHaejo.controller;

import jakarta.validation.Valid;
import jeiu.capstone.jongGangHaejo.domain.File;
import jeiu.capstone.jongGangHaejo.domain.Post;
import jeiu.capstone.jongGangHaejo.dto.response.PostDetailResponseDto;
import jeiu.capstone.jongGangHaejo.dto.response.PostListResponseDto;
import jeiu.capstone.jongGangHaejo.service.FileService;
import jeiu.capstone.jongGangHaejo.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import jeiu.capstone.jongGangHaejo.dto.request.PostCreateDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor //생성자 lombok
@Slf4j //logging을 위한 lombok
@RestController //RestAPI로 응답하기 위함
public class PostController {

    //Hello World가 브라우저에 출력된다(text/plain형식)
    @GetMapping("/test")
    public String getTest() {
        return "안녕하세요 종강해조 프로젝트입니다";
    }

    //DTO를 통해 값을 가져오는 방식


    private final PostService postService;
    private final FileService fileService;

    @PostMapping("/post") //if문을 통해 예외를 던지는 방식대신 Dto에 @NotBlank를 걸고 @Valid를 통해 검증하는 방식 채택
    public Map<String, String> createPost(
            @RequestPart("post") @Valid PostCreateDto postCreateDto,  // 게시물 데이터
            @RequestPart("files") List<MultipartFile> files,          // 첨부 파일들
            BindingResult result
            //@RequestBody @Valid PostCreateDto params, BindingResult result
    ) {
        // 유효성 검증 실패 시 처리
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return errors; // 에러 메시지를 반환
        }

        Post post = Post.builder()
                .title(postCreateDto.getTitle())
                .content(postCreateDto.getContent())
                .team(postCreateDto.getTeam())
                .youtubelink(postCreateDto.getYoutubelink())
                .build();

        // File 리스트 생성
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String fileUrl = fileService.uploadFile(file);  // S3에 파일 업로드하고 URL 받기

                // File 엔티티 생성 및 Post와 연관 설정
                File fileEntity = File.builder()
                        .fileName(file.getOriginalFilename())
                        .s3Path(fileUrl)
                        .fileType(file.getContentType())
                        .fileSize(file.getSize())
                        .build();

                post.addFile(fileEntity);
            }
        }

        // Post 엔티티 생성 시 파일 리스트도 함께 설정


        // 4. Post 엔티티 저장 (Post가 저장되면 연관된 File들도 함께 저장됨)
        postService.savePost(post);


        return Map.of("message", "게시물이 성공적으로 생성되었습니다.");
    }

    // 게시물 목록 조회
    @GetMapping("/posts")
    public ResponseEntity<Page<PostListResponseDto>> getAllPosts(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostListResponseDto> posts = postService.getAllPosts(pageable);
        return ResponseEntity.ok(posts);
    }

    // 단일 게시물 조회
    @GetMapping("posts/{id}")
    public ResponseEntity<PostDetailResponseDto> getPost(@PathVariable("id") Long id) {
        PostDetailResponseDto post = postService.getPost(id);
        return ResponseEntity.ok(post);
    }

    // 게시물 검색
    @GetMapping("posts/search")
    public ResponseEntity<Page<PostListResponseDto>> searchPosts(
            @RequestParam String keyword,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostListResponseDto> posts = postService.searchPostsByTitle(keyword, pageable);
        return ResponseEntity.ok(posts);
    }
}