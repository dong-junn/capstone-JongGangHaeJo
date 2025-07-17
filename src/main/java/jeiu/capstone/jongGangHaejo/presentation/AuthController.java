package jeiu.capstone.jongGangHaejo.presentation;

import jakarta.validation.Valid;
import jeiu.capstone.jongGangHaejo.dto.auth.FindIdRequestDto;
import jeiu.capstone.jongGangHaejo.dto.auth.FindIdVerifyDto;
import jeiu.capstone.jongGangHaejo.dto.auth.FindPasswordRequestDto;
import jeiu.capstone.jongGangHaejo.dto.auth.NewPasswordRequestDto;
import jeiu.capstone.jongGangHaejo.dto.auth.PasswordResetTokenResponse;
import jeiu.capstone.jongGangHaejo.dto.auth.PasswordResetVerifyDto;
import jeiu.capstone.jongGangHaejo.dto.email.EmailRequestDto;
import jeiu.capstone.jongGangHaejo.dto.email.VerificationRequestDto;
import jeiu.capstone.jongGangHaejo.dto.form.user.SignUpDto;
import jeiu.capstone.jongGangHaejo.security.dto.LoginDto;
import jeiu.capstone.jongGangHaejo.security.dto.response.member.SignInResponse;
import jeiu.capstone.jongGangHaejo.security.dto.response.member.SignUpResponse;
import jeiu.capstone.jongGangHaejo.security.service.SecurityService;
import jeiu.capstone.jongGangHaejo.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final SecurityService securityService;
    private final EmailService emailService;

    // 회원가입용 이메일 인증
    @PostMapping("/sign-up/email")
    public ResponseEntity<String> sendSignUpVerificationEmail(@RequestBody @Valid EmailRequestDto request) {
        emailService.sendVerificationEmail(request.getEmail());
        return ResponseEntity.ok("인증 코드가 발송되었습니다.");
    }

    @PostMapping("/sign-up/email/verify")
    public ResponseEntity<String> verifySignUpEmail(@RequestBody @Valid VerificationRequestDto request) {
        emailService.verifyEmail(request.getEmail(), request.getCode());
        return ResponseEntity.ok("이메일 인증이 완료되었습니다.");
    }

    @PostMapping("/sign-up")
    public ResponseEntity<SignUpResponse> signUp(@RequestBody @Valid SignUpDto signUpDto) {
        securityService.createUser(signUpDto);
        return ResponseEntity.ok(new SignUpResponse("회원가입이 완료되었습니다."));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<SignInResponse> signIn(@RequestBody @Valid LoginDto loginDto) {
        String token = securityService.login(loginDto);
        return ResponseEntity.ok(new SignInResponse(token, "로그인이 완료되었습니다"));
    }

    // 아이디 찾기
    @PostMapping("/find-id/request")
    public ResponseEntity<String> requestFindId(@RequestBody @Valid FindIdRequestDto request) {
        securityService.requestFindId(request.getEmail());
        return ResponseEntity.ok("인증 코드가 이메일로 발송되었습니다.");
    }

    @PostMapping("/find-id/verify")
    public ResponseEntity<String> verifyFindId(@RequestBody @Valid FindIdVerifyDto request) {
        String id = securityService.verifyAndFindId(request.getEmail(), request.getCode());
        return ResponseEntity.ok("회원님의 아이디는 " + id + " 입니다.");
    }

    // 비밀번호 찾기
    @PostMapping("/find-password/request")
    public ResponseEntity<String> requestPasswordReset(@RequestBody @Valid FindPasswordRequestDto request) {
        securityService.requestPasswordReset(request.getId(), request.getEmail());
        return ResponseEntity.ok("인증 코드가 이메일로 발송되었습니다.");
    }

    @PostMapping("/find-password/verify")
    public ResponseEntity<PasswordResetTokenResponse> verifyPasswordResetCode(
            @RequestBody @Valid PasswordResetVerifyDto request) {
        String token = securityService.verifyPasswordResetCode(request.getId(), request.getEmail(), request.getCode());
        return ResponseEntity.ok(new PasswordResetTokenResponse(token));
    }

    @PostMapping("/find-password/reset")
    public ResponseEntity<String> resetPassword(
            @RequestHeader("Password-Reset-Token") String token,
            @RequestBody @Valid NewPasswordRequestDto request) {
        securityService.resetPassword(token, request.getNewPassword());
        return ResponseEntity.ok("비밀번호가 변경되었습니다.");
    }
}
