package jeiu.capstone.jongGangHaejo.security.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor @AllArgsConstructor
public class LoginDto {

    @NotEmpty(message = "아이디를 입력하지 않으셨습니다.")
    private String id;

    @NotEmpty(message = "비밀번호를 입력하지 않으셨습니다.")
    private String password;
}