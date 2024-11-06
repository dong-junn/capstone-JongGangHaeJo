package jeiu.capstone.jongGangHaejo.service.user.security;

import jeiu.capstone.jongGangHaejo.domain.user.Role;
import jeiu.capstone.jongGangHaejo.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if ("test".equals(username)) {
            return (UserDetails) User.builder()
                    .name(username)
                    .password(passwordEncoder.encode("password"))
                    .role(Role.ROLE_USER)
                    .build();
        }
        throw new UsernameNotFoundException("User not found");
    }
}
