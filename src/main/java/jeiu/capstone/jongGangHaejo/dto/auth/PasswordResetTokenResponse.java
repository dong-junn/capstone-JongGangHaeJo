package jeiu.capstone.jongGangHaejo.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PasswordResetTokenResponse {
    private String token;
} 