package jeiu.capstone.jongGangHaejo.scheduler;

import jeiu.capstone.jongGangHaejo.repository.EmailVerificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailVerificationCleanupScheduler {

    private final EmailVerificationRepository verificationRepository;

    // 매일 자정에 실행
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cleanupExpiredVerifications() {
        try {
            verificationRepository.deleteAllExpired(LocalDateTime.now());
            log.info("만료된 이메일 인증 정보 정리 완료");
        } catch (Exception e) {
            log.error("만료된 이메일 인증 정보 정리 중 오류 발생: {}", e.getMessage());
        }
    }
} 