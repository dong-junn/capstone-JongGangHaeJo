package jeiu.capstone.jongGangHaejo.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserRequestDto {

    private final String id;
    private final String password;

    @Builder
    public UserRequestDto(String id, String password) {
        this.id = id;
        this.password = password;
    }
}
