package jeiu.capstone.jongGangHaejo.repository;

import jeiu.capstone.jongGangHaejo.config.SecurityConfig;
import jeiu.capstone.jongGangHaejo.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Rollback(false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SecurityConfig securityConfig;

    @Test
    void 리파지토리_비밀번호_해시화_테스트() {
        //given
        User user = User.builder()
                .id("testID")
                .password(securityConfig.bCryptPasswordEncoder().encode("testPassword"))
                .build();


        //when
        User savedUser = userRepository.save(user);

        //then
        Assertions.assertThat(user.getId()).isEqualTo("testID");
        assertTrue(securityConfig.bCryptPasswordEncoder().matches("testPassword", savedUser.getPassword()));

    }

}