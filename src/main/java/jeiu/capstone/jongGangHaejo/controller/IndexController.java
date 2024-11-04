package jeiu.capstone.jongGangHaejo.controller;

import jeiu.capstone.jongGangHaejo.config.UserConfig;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.attribute.UserPrincipal;

@RestController
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "종강해조 게시판 메인 페이지";
    }

    @GetMapping("/user")
    public String user(@AuthenticationPrincipal UserConfig user) {
        return "로그인 회원: " + user.getUsername();
    }
}
