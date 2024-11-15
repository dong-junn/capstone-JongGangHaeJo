package jeiu.capstone.jongGangHaejo.dto.admin.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jeiu.capstone.jongGangHaejo.domain.user.Role;
import jeiu.capstone.jongGangHaejo.domain.user.User;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserCreateDto {
    @NotBlank(message = "아이디는 필수 입력값입니다")
    private String id;

    @NotBlank(message = "비밀번호는 필수 입력값입니다")
    private String password;

    @NotBlank(message = "이름은 필수 입력값입니다")
    private String name;

    @NotEmpty(message = "역할은 최소 하나 이상 지정되어야 합니다")
    private Set<Role> roles;

    public User toEntity() {
        return User.builder()
                .id(id)
                .password(password)
                .name(name)
                .roles(roles)
                .build();
    }
}