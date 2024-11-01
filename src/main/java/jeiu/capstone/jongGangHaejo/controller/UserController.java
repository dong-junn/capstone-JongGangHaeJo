package jeiu.capstone.jongGangHaejo.controller;

import jeiu.capstone.jongGangHaejo.dto.form.UserFormDto;
import jeiu.capstone.jongGangHaejo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("sign-up")
    public Map<String, String> singUp(@RequestBody UserFormDto form) {
        userService.createUser(form);
        Map<String, String> map = new HashMap<>();
        map.put("message", "종강해조 게시판의 회원이 되신 것을 환영합니다!");
        return map;
    }
}
