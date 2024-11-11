package jeiu.capstone.jongGangHaejo.security.service;

import jeiu.capstone.jongGangHaejo.domain.user.Role;
import jeiu.capstone.jongGangHaejo.domain.user.User;
import jeiu.capstone.jongGangHaejo.dto.form.user.SignUpDto;
import jeiu.capstone.jongGangHaejo.repository.UserRepository;
import jeiu.capstone.jongGangHaejo.security.config.UserConfig;
import jeiu.capstone.jongGangHaejo.security.dto.LoginDto;
import jeiu.capstone.jongGangHaejo.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public User createUser(SignUpDto form) {
        User user = User.builder()
                .id(form.getId())
                .password(passwordEncoder.encode(form.getPassword()))
                .name(form.getUsername())
                .role(Role.ROLE_USER)
                .build();

        return userRepository.save(user);
    }


    // 로그인 처리
    public String login(LoginDto loginDto) {
        // 1. 사용자 찾기
        User user = userRepository.findById(loginDto.getId())
                .orElseThrow(() -> new UsernameNotFoundException(loginDto.getId() + "로 가입된 회원이 없습니다"));

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        // 3. UserConfig 생성 및 토큰 생성
        UserConfig userConfig = new UserConfig(user);

        return jwtUtil.generateToken(
                user.getId(),
                userConfig.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(","))
        );
    }
}
