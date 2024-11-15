package jeiu.capstone.jongGangHaejo.service.admin;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityNotFoundException;
import jeiu.capstone.jongGangHaejo.domain.user.QUser;
import jeiu.capstone.jongGangHaejo.domain.user.User;
import jeiu.capstone.jongGangHaejo.dto.admin.user.UserUpdateDto;
import jeiu.capstone.jongGangHaejo.exception.ResourceNotFoundException;
import jeiu.capstone.jongGangHaejo.exception.common.CommonErrorCode;
import jeiu.capstone.jongGangHaejo.repository.admin.user.AdminUserRepository;
import jeiu.capstone.jongGangHaejo.dto.admin.user.UserSearchCondition;
import jeiu.capstone.jongGangHaejo.repository.admin.user.AdminUserRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminUserService {
    private final AdminUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<User> findUsers(UserSearchCondition condition, Pageable pageable) {
        return userRepository.findBySearchCondition(condition, pageable);
    }

    public Optional<User> findUser(String id) {
        return userRepository.findById(id);
    }

    @Transactional
    public User saveUser(User user) {
        validateDuplicateUser(user.getId());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    private void validateDuplicateUser(String id) {
        if (userRepository.findById(id).isPresent()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    @Transactional
    public User updateUser(String id, UserUpdateDto updateDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("없는 회원입니다"));

        if (StringUtils.hasText(updateDto.getName())) {
            existingUser.setName(updateDto.getName());
        }
        if (StringUtils.hasText(updateDto.getPassword())) {
            existingUser.setPassword(passwordEncoder.encode(updateDto.getPassword()));
        }
        if (updateDto.getRoles() != null && !updateDto.getRoles().isEmpty()) {
            existingUser.setRoles(updateDto.getRoles());
        }

        return userRepository.save(existingUser);
    }

    @Transactional
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
}