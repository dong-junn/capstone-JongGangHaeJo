package jeiu.capstone.jongGangHaejo.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor @AllArgsConstructor
public class LoginDto {
    private String id;
    private String password;
}