package jeiu.capstone.jongGangHaejo.controller;

import jeiu.capstone.jongGangHaejo.config.UserConfig;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecurityController {

    @GetMapping("/user")
    public String user(@AuthenticationPrincipal UserConfig user) {
        return "로그인 회원: " + user.getUsername();
    }
}
