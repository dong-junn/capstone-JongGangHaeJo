package jeiu.capstone.jongGangHaejo.presentation;

import jeiu.capstone.jongGangHaejo.security.config.UserConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class SecurityController {

    @GetMapping("/user")
    public String user(@AuthenticationPrincipal UserConfig user) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("인증정보: {}", auth);
        log.info("Principal 타입: {}", auth != null ? auth.getPrincipal().getClass() : "null");

        return "로그인 회원: " + user.getUsername();
    }

    @GetMapping("/admin")
    public String admin() {
        return "어드민 페이지";
    }
}
