package jeiu.capstone.jongGangHaejo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import jeiu.capstone.jongGangHaejo.dto.request.PostCreateDto;

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
    @PostMapping("/posts") //if문을 통해 예외를 던지는 방식대신 Dto에 @NotBlank를 걸고 @Valid를 통해 검증하는 방식 채택
    public Map<String, String> post(@RequestBody @Valid PostCreateDto params) {
        return Map.of();
    }
}
