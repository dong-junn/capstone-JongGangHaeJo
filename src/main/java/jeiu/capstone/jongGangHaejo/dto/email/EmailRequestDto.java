package jeiu.capstone.jongGangHaejo.dto.email;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class EmailRequestDto {
    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    private String email;
}

