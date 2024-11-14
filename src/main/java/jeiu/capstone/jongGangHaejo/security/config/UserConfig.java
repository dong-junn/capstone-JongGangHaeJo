package jeiu.capstone.jongGangHaejo.security.config;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;


import java.util.stream.Collectors;

public class UserConfig extends User {
    public UserConfig(jeiu.capstone.jongGangHaejo.domain.user.User user) {
        super(
                user.getName(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getValue()))
                        .collect(Collectors.toList())
        );
    }
}