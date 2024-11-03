package jeiu.capstone.jongGangHaejo.dto.form.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class SignUpDto {

    @NotEmpty(message = "id를 입력하지 않으셨습니다!")
    private final String id;

    @NotEmpty(message = "비밀번호를 입력하지 않으셨습니다!")
    private final String password;

    @NotEmpty(message = "이름을 입력하지 않으셨습니다!")
    private final String username;
}
