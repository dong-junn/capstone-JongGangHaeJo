package jeiu.capstone.jongGangHaejo.controller;

import jakarta.validation.Valid;
import jeiu.capstone.jongGangHaejo.domain.Post;
import jeiu.capstone.jongGangHaejo.service.FileService;
import jeiu.capstone.jongGangHaejo.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @PostMapping("/test")
    public String postTest() {
        return "안녕하세요 종강해조 프로젝트입니다";
    }

/*
    @PostMapping("/test")
    public String postTest(@RequestParam String title, @RequestParam String content) {
        log.info("title={}, content={}", title, content);
        return "Hello World";
    }
 */

/*
    @PostMapping("/test")  //Map을 이용하여 넘기는 방법도 있다
    public String postTest(@RequestParam Map<String, String> params) {
        log.info("params={}", params);
        String title = params.get("title"); //추후 맵에서 꺼내 사용할 수도 있다
        return "Hello World";
    }
    //클래스를 정의하여 넘기는 방법이 조금 더 유지보수 하기 좋아보인다 -> dto.request 패키지에 정의하겠음
 */

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

        // jeiu.capstone.jongGangHaejo.domain.File 리스트 생성
        List<jeiu.capstone.jongGangHaejo.domain.File> fileList = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                // AWS S3에 파일 업로드하고 URL 받기
                String fileUrl = fileService.uploadFile(file);

                // jeiu.capstone.jongGangHaejo.domain.File 객체 생성
                jeiu.capstone.jongGangHaejo.domain.File fileEntity = jeiu.capstone.jongGangHaejo.domain.File.builder()
                        .fileName(file.getOriginalFilename())
                        .s3Path(fileUrl)
                        .fileType(file.getContentType())
                        .fileSize(file.getSize())
                        .build();

                // fileList에 추가
                fileList.add(fileEntity);
            }
        }

        // Post 엔티티 생성 시 파일 리스트도 함께 설정
        Post post = Post.builder()
                .title(postCreateDto.getTitle())
                .content(postCreateDto.getContent())
                .team(postCreateDto.getTeam())
                .youtubelink(postCreateDto.getYoutubelink())
                .files(fileList)  // jeiu.capstone.jongGangHaejo.domain.File 리스트 추가
                .build();

        // 4. Post 엔티티 저장 (Post가 저장되면 연관된 File들도 함께 저장됨)
        postService.savePost(post);


        return Map.of("message", "게시물이 성공적으로 생성되었습니다.");
    }
}