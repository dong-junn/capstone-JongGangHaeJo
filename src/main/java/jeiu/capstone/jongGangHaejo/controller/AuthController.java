package jeiu.capstone.jongGangHaejo.controller;

import jeiu.capstone.jongGangHaejo.domain.user.User;
import jeiu.capstone.jongGangHaejo.dto.form.user.SignUpDto;
import jeiu.capstone.jongGangHaejo.security.dto.LoginDto;
import jeiu.capstone.jongGangHaejo.security.dto.response.member.SignInResponse;
import jeiu.capstone.jongGangHaejo.security.dto.response.member.SignUpResponse;
import jeiu.capstone.jongGangHaejo.security.service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping()
public class AuthController {

    private final SecurityService securityService;

    @PostMapping("/sign-up")
    public ResponseEntity<SignUpResponse> signUp(@RequestBody SignUpDto signUpDto) {
        User user = securityService.createUser(signUpDto);
        return ResponseEntity.ok(new SignUpResponse("회원가입이 완료되었습니다."));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<SignInResponse> signIn(@RequestBody LoginDto loginDto) {
        String token = securityService.login(loginDto);
        return ResponseEntity.ok(new SignInResponse(token));
    }

}
