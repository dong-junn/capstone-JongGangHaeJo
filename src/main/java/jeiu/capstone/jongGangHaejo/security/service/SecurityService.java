package jeiu.capstone.jongGangHaejo.security.service;

import jakarta.persistence.EntityNotFoundException;
import jeiu.capstone.jongGangHaejo.domain.user.Role;
import jeiu.capstone.jongGangHaejo.domain.user.User;
import jeiu.capstone.jongGangHaejo.dto.form.user.SignUpDto;
import jeiu.capstone.jongGangHaejo.exception.VerificationException;
import jeiu.capstone.jongGangHaejo.exception.common.CommonErrorCode;
import jeiu.capstone.jongGangHaejo.exception.UnauthorizedException;
import jeiu.capstone.jongGangHaejo.repository.EmailVerificationRepository;
import jeiu.capstone.jongGangHaejo.repository.UserRepository;
import jeiu.capstone.jongGangHaejo.security.dto.LoginDto;
import jeiu.capstone.jongGangHaejo.security.jwt.JwtUtil;
import jeiu.capstone.jongGangHaejo.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final EmailVerificationRepository verificationRepository;

    @Transactional
    public User createUser(SignUpDto form) {
        //이메일 인증 확인
        boolean isVerified = verificationRepository.existsByEmailAndVerifiedTrue(form.getEmail());
        if (!isVerified) {
            throw new VerificationException("이메일 인증이 완료되지 않았습니다.");
        }

        // ID 중복 검사
        if (userRepository.findById(form.getId()).isPresent()) {
            throw new IllegalStateException("이미 사용중인 아이디입니다.");
        }
        
        // 이름 중복 검사
        if (userRepository.findByName(form.getUsername()).isPresent()) {
            throw new IllegalStateException("이미 사용중인 이름입니다.");
        }
        
        // 이메일 중복 검사
        if (userRepository.findByEmail(form.getEmail()).isPresent()) {
            throw new IllegalStateException("이미 사용중인 이메일입니다.");
        }

        Set<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_USER);

        User user = User.builder()
                .id(form.getId())
                .password(passwordEncoder.encode(form.getPassword()))
                .name(form.getUsername())
                .email(form.getEmail())
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

        return jwtUtil.generateToken(
            user.getId(), 
            user.getName(), 
            authorities
        );
    }

    public String findUserId(String name, String email) {
        return userRepository.findByNameAndEmail(name, email)
                .map(User::getId)
                .orElseThrow(() -> new EntityNotFoundException("일치하는 회원 정보가 없습니다."));
    }

    @Transactional
    public void requestPasswordReset(String id, String email) {
        User user = userRepository.findByIdAndEmail(id, email)
                .orElseThrow(() -> new EntityNotFoundException("일치하는 회원 정보가 없습니다."));
        
        emailService.sendVerificationEmail(email); // 기존 이메일 인증 로직 재사용
    }

    @Transactional
    public String verifyPasswordResetCode(String id, String email, String code) {
        // 사용자 확인
        User user = userRepository.findByIdAndEmail(id, email)
                .orElseThrow(() -> new EntityNotFoundException("일치하는 회원 정보가 없습니다."));

        // 인증 코드 확인
        boolean verified = emailService.verifyEmail(email, code);
        if (!verified) {
            throw new VerificationException("인증에 실패했습니다.");
        }

        // 비밀번호 재설정용 토큰 생성 (짧은 유효기간 설정)
        return jwtUtil.generatePasswordResetToken(id);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        // 토큰 검증 및 사용자 ID 추출
        String userId = jwtUtil.validatePasswordResetTokenAndGetUserId(token);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
                
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public void requestFindId(String email) {
        // 사용자 존재 여부 확인
        userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("일치하는 회원 정보가 없습니다."));
        
        // 인증 코드 발송
        emailService.sendVerificationEmail(email);
    }

    @Transactional
    public String verifyAndFindId(String email, String code) {
        // 사용자 확인
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("일치하는 회원 정보가 없습니다."));

        // 인증 코드 확인
        boolean verified = emailService.verifyEmail(email, code);
        if (!verified) {
            throw new VerificationException("인증에 실패했습니다.");
        }

        return user.getId();
    }
}
