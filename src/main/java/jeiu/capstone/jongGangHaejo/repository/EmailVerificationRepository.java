package jeiu.capstone.jongGangHaejo.repository;

import jeiu.capstone.jongGangHaejo.domain.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    
    // 이메일로 가장 최근 인증 정보 조회
    Optional<EmailVerification> findTopByEmailOrderByIdDesc(String email);
    
    // 이메일로 미인증 상태인 인증 정보 조회
    Optional<EmailVerification> findByEmailAndVerifiedFalse(String email);
    
    // 만료된 인증 정보 조회
    @Query("SELECT ev FROM EmailVerification ev WHERE ev.expiryDate < :now AND ev.verified = false")
    List<EmailVerification> findAllExpired(@Param("now") LocalDateTime now);
    
    // 이메일과 인증 코드로 유효한 인증 정보 조회
    @Query("SELECT ev FROM EmailVerification ev " +
           "WHERE ev.email = :email " +
           "AND ev.verificationCode = :code " +
           "AND ev.expiryDate > :now " +
           "AND ev.verified = false")
    Optional<EmailVerification> findValidVerification(
            @Param("email") String email,
            @Param("code") String code,
            @Param("now") LocalDateTime now
    );
    
    // 이메일로 인증 완료 여부 확인
    boolean existsByEmailAndVerifiedTrue(String email);
    
    // 만료된 인증 정보 삭제
    @Query("DELETE FROM EmailVerification ev WHERE ev.expiryDate < :now")
    void deleteAllExpired(@Param("now") LocalDateTime now);
}
