package jeiu.capstone.jongGangHaejo.presentation;

import jakarta.validation.Valid;
import jeiu.capstone.jongGangHaejo.dto.email.EmailRequestDto;
import jeiu.capstone.jongGangHaejo.dto.email.VerificationRequestDto;
import jeiu.capstone.jongGangHaejo.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/verify")
    public ResponseEntity<String> sendVerificationEmail(@RequestBody @Valid EmailRequestDto request) {
        emailService.sendVerificationEmail(request.getEmail());
        return ResponseEntity.ok("인증 코드가 발송되었습니다.");
    }

    @PostMapping("/verify/code")
    public ResponseEntity<String> verifyCode(@RequestBody @Valid VerificationRequestDto request) {
        boolean verified = emailService.verifyEmail(request.getEmail(), request.getCode());
        return ResponseEntity.ok("이메일 인증이 완료되었습니다.");
    }
} 