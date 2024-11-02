package jeiu.capstone.jongGangHaejo.dto.form;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class SignUpDto {

    private final String id;
    private final String password;
    private final String name;
}
