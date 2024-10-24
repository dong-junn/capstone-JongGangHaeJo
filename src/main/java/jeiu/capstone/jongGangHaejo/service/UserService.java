package jeiu.capstone.jongGangHaejo.service;

import jeiu.capstone.jongGangHaejo.domain.User;
import jeiu.capstone.jongGangHaejo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String join(User user) {
        userRepository.save(user);
        return user.getId();
    }
}
