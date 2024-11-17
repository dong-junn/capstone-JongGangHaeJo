package jeiu.capstone.jongGangHaejo.security.dto.response.member;

import lombok.*;

@Getter
@RequiredArgsConstructor
public class SignInResponse {

    private final String token;
    private final String message;
}
