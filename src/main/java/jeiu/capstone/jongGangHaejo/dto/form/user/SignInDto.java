package jeiu.capstone.jongGangHaejo.dto.form.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class SignInDto {

    private final String id;
    private final String password;
}
