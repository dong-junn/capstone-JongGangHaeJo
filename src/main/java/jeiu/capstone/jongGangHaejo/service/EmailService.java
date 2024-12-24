package jeiu.capstone.jongGangHaejo.service;

import jakarta.mail.internet.MimeMessage;
import jeiu.capstone.jongGangHaejo.domain.EmailVerification;
import jeiu.capstone.jongGangHaejo.exception.EmailSendException;
import jeiu.capstone.jongGangHaejo.exception.VerificationException;
import jeiu.capstone.jongGangHaejo.repository.EmailVerificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailVerificationRepository verificationRepository;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    private static final int VERIFICATION_CODE_LENGTH = 6;
    private static final int EXPIRATION_MINUTES = 5;

    @Async("emailExecutor")
    @Transactional
    public CompletableFuture<Void> sendVerificationEmail(String toEmail) {
        try {
            // 이전 인증 코드 만료 처리
            verificationRepository.findByEmailAndVerifiedFalse(toEmail)
                    .ifPresent(verificationRepository::delete);
            
            String verificationCode = generateVerificationCode();
            
            EmailVerification verification = EmailVerification.builder()
                    .email(toEmail)
                    .verificationCode(verificationCode)
                    .expirationMinutes(EXPIRATION_MINUTES)
                    .build();
                    
            verificationRepository.save(verification);
            
            // 이메일 전송
            sendEmail(toEmail, verificationCode);
            
            log.info("인증 이메일 발송 완료: {}", toEmail);
            return CompletableFuture.completedFuture(null);
            
        } catch (Exception e) {
            log.error("이메일 발송 실패: {}", e.getMessage());
            throw new EmailSendException("이메일 발송에 실패했습니다: " + e.getMessage());
        }
    }
    
    private void sendEmail(String toEmail, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("JEIU 캡스톤 이메일 인증");
            helper.setText(createEmailContent(code), true);
            
            mailSender.send(message);
        } catch (Exception e) {
            throw new EmailSendException("이메일 전송 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    @Transactional
    public boolean verifyEmail(String email, String code) {
        EmailVerification verification = verificationRepository
                .findTopByEmailOrderByIdDesc(email)
                .orElseThrow(() -> new VerificationException("인증 정보를 찾을 수 없습니다."));
                
        if (verification.isExpired()) {
            throw new VerificationException("인증 코드가 만료되었습니다.");
        }
        
        if (!verification.getVerificationCode().equals(code)) {
            throw new VerificationException("잘못된 인증 코드입니다.");
        }
        
        verification.verify();
        return true;
    }
    
    private String generateVerificationCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        
        for (int i = 0; i < VERIFICATION_CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        
        return code.toString();
    }
    
    private String createEmailContent(String code) {
        return String.format("""
                <div style='margin:100px;'>
                    <h1>이메일 인증 코드</h1>
                    <br>
                    <p>안녕하세요.</p>
                    <p>아래 인증 코드를 입력해주세요.</p>
                    <br>
                    <div style='font-size:24px; letter-spacing: 2px;'>
                        <strong>%s</strong>
                    </div>
                    <br>
                    <p>인증 코드는 5분간 유효합니다.</p>
                </div>
                """, code);
    }
} 