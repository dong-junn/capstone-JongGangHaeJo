package jeiu.capstone.jongGangHaejo.dto.form.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class SignInDto {

    @NotEmpty(message = "아이디를 입력하지 않으셨습니다!")
    private final String id;

    @NotEmpty(message = "아이디를 입력하지 않으셨습니다!")
    private final String password;
}
