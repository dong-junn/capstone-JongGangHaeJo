//package jeiu.capstone.jongGangHaejo.repository;
//
//import jeiu.capstone.jongGangHaejo.config.QueryDslTestConfig;
//import jeiu.capstone.jongGangHaejo.domain.user.User;
//import jeiu.capstone.jongGangHaejo.dto.form.user.SignUpDto;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
//@DataJpaTest
//@Import(QueryDslTestConfig.class)
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//
//public class UserRepositoryTest {
//
//    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//
//    @Autowired
//    private UserRepository userRepository;
//
//    String testID = "testID";
//    String testPWD = "testPWD";
//    String testName = "testName";
//
//
//
//    @Test
//    @DisplayName("user table에 저장되는지 테스트")
//    void USER_SAVE_테스트() {
//        //given
//        SignUpDto form = new SignUpDto(testID, testPWD, testName);
//        User user = User.builder()
//                .id(form.getId())
//                .password(encoder.encode(form.getPassword()))
//                .name(form.getUsername())
//                .build();
//
//        //when
//        User savedUser = userRepository.save(user);
//
//        //then
//        Assertions.assertThat(savedUser.getId()).isEqualTo(form.getId());
//        Assertions.assertThat(encoder.matches(form.getPassword(), savedUser.getPassword())).isTrue();
//        Assertions.assertThat(savedUser.getName()).isEqualTo(form.getUsername());
//
//    }
//}
