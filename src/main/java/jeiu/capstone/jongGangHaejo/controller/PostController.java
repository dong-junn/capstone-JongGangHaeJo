package jeiu.capstone.jongGangHaejo.controller;

import jeiu.capstone.jongGangHaejo.dto.request.PostCreate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/test")
    public String post(@RequestBody PostCreate params) {
        log.info("params={}", params.toString());
        return "Hello World";
    }
}
