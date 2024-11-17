package jeiu.capstone.jongGangHaejo.dto.admin.user;

import jeiu.capstone.jongGangHaejo.domain.user.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter @Setter
@Builder
public class UserUpdateDto {

    private String name;
    private String password;
    private Set<Role> roles;

}
