package jeiu.capstone.jongGangHaejo.dto.form.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class SignUpDto {

    @NotEmpty(message = "id를 입력하지 않으셨습니다!")
    @Pattern(
        regexp = "^[a-zA-Z0-9]{4,}$",
        message = "아이디는 4글자 이상의 영문자와 숫자만 사용 가능합니다."
    )
    private final String id;

    @NotEmpty(message = "비밀번호를 입력하지 않으셨습니다!")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
        message = "비밀번호는 8자 이상이며 영문, 숫자, 특수문자를 포함해야 합니다."
    )
    private final String password;

    @NotEmpty(message = "이름을 입력하지 않으셨습니다!")
    private final String username;

    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    @NotEmpty(message = "이메일을 입력하지 않으셨습니다!")
    private final String email;
}
