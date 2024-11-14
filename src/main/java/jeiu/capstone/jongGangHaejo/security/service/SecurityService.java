package jeiu.capstone.jongGangHaejo.security.service;

import jeiu.capstone.jongGangHaejo.domain.user.Role;
import jeiu.capstone.jongGangHaejo.domain.user.User;
import jeiu.capstone.jongGangHaejo.dto.form.user.SignUpDto;
import jeiu.capstone.jongGangHaejo.exception.common.CommonErrorCode;
import jeiu.capstone.jongGangHaejo.exception.UnauthorizedException;
import jeiu.capstone.jongGangHaejo.repository.UserRepository;
import jeiu.capstone.jongGangHaejo.security.dto.LoginDto;
import jeiu.capstone.jongGangHaejo.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public User createUser(SignUpDto form) {
        User user = User.builder()
                .id(form.getId())
                .password(passwordEncoder.encode(form.getPassword()))
                .name(form.getUsername())
                .roles(Collections.singleton(Role.ROLE_USER))
                .build();

        return userRepository.save(user);
    }


    // 로그인 처리
    public String login(LoginDto loginDto) {
        //사용자 찾기
        User user = userRepository.findById(loginDto.getId())
                .orElseThrow(() -> new UnauthorizedException("존재하지 않는 아이디입니다.", CommonErrorCode.LOGIN_ID_NOT_FOUND));

        //비밀번호 검증
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("비밀번호가 일치하지 않습니다.", CommonErrorCode.LOGIN_PASSWORD_INVALID);
        }

        //토큰 생성
        String authorities = user.getRoles().stream()
                .map(Role::getValue)
                .collect(Collectors.joining(","));

        return jwtUtil.generateToken(user.getId(), authorities);
    }
}
