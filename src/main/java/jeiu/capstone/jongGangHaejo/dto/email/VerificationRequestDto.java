package jeiu.capstone.jongGangHaejo.dto.email;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VerificationRequestDto {
    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    private String email;
    
    @NotBlank(message = "인증 코드를 입력해주세요.")
    @Pattern(regexp = "^[0-9]{6}$", message = "6자리 숫자를 입력해주세요.")
    private String code;
}
