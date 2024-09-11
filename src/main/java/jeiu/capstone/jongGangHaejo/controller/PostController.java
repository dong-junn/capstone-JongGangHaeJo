package jeiu.capstone.jongGangHaejo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController //RestAPI로 응답하기 위함
public class PostController {

    //Hello World가 브라우저에 출력된다(text/plain형식)
    @GetMapping("/test")
    public String get() {
        return "Hello World";
    }
}
