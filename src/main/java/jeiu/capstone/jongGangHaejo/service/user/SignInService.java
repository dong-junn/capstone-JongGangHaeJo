package jeiu.capstone.jongGangHaejo.service.user;

import jeiu.capstone.jongGangHaejo.config.SignInConfig;
import jeiu.capstone.jongGangHaejo.domain.user.User;
import jeiu.capstone.jongGangHaejo.dto.form.user.SignInDto;
import jeiu.capstone.jongGangHaejo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SignInService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    //로그인시 없는 id로 로그인 할 경우 처리
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> {
            User user = userRepository.findById(username)
                    .orElseThrow(() -> new UsernameNotFoundException(username + " 회원을 찾을 수 없습니다. 회원가입 후 진행해주세요."));
            return new SignInConfig(user);
        };
    }

    //로그인 처리
    public void processLogin(SignInDto form) {
        UserDetails userDetails = userDetailsService(userRepository)
                .loadUserByUsername(form.getId());

        if (!passwordEncoder.matches(form.getPassword(), userDetails.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
