package jeiu.capstone.jongGangHaejo.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;

public class UserConfig extends User {

    public UserConfig(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public UserConfig(jeiu.capstone.jongGangHaejo.domain.user.User user) {
        super(user.getId(), user.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
