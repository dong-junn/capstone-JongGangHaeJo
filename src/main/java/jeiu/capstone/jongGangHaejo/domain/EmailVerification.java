package jeiu.capstone.jongGangHaejo.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailVerification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String verificationCode;
    
    @Column(nullable = false)
    private LocalDateTime expiryDate;
    
    @Column(nullable = false)
    private boolean verified = false;
    
    @Builder
    public EmailVerification(String email, String verificationCode, int expirationMinutes) {
        this.email = email;
        this.verificationCode = verificationCode;
        this.expiryDate = LocalDateTime.now().plusMinutes(expirationMinutes);
    }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
    
    public void verify() {
        this.verified = true;
    }
} 