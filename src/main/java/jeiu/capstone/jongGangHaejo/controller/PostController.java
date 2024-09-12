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
        return "Hello World";
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
    @PostMapping("/test") //if문을 통해 예외를 던지는 방식대신 Dto에 @NotBlank를 걸고 @Valid를 통해 검증하는 방식 채택
    public Map<String, String> post(@RequestBody @Valid PostCreateDto params, BindingResult result) {
        //PostCreate에서 사용한 @NotBlank검증을 진행하기 위해 @Valid를 사용해줘야 한다
        //@Valid를 이용하여 검증하고 싶었으나 400이 떨어지며 테스트 실패 -> BindingResult 사용
        if (result.hasErrors()) {
            List<FieldError> fieldErrors = result.getFieldErrors(); //@Valid를 통해 담기는 에러 값을 list형태로 저장
            FieldError firstFieldError = fieldErrors.get(0); //0번째 값을 가져온다
            String errorFieldName = firstFieldError.getField(); //title
            String errorMessage = firstFieldError.getDefaultMessage(); //에러 메세지

            //error맵에 담아서 리턴
            HashMap<String, String> error = new HashMap<>();
            error.put(errorFieldName, errorMessage);
            return error;

        }
        log.info("params={}", params.toString());
        return Map.of();
    }
}
