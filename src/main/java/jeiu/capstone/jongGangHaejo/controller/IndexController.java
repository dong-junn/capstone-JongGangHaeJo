package jeiu.capstone.jongGangHaejo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class IndexController {

    @GetMapping
    public String index() {
        return "종강해조 게시판 메인 페이지";
    }
}
