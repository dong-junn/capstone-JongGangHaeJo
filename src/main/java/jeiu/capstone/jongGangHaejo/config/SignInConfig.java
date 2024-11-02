package jeiu.capstone.jongGangHaejo.config;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public class SignInConfig extends User {

    private final String userId;

    public SignInConfig(jeiu.capstone.jongGangHaejo.domain.user.User user) {
        super(user.getId(), user.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
        this.userId = user.getId();
    }

    public String getUserId() {
        return userId;
    }
}
