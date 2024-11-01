package jeiu.capstone.jongGangHaejo.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jeiu.capstone.jongGangHaejo.dto.form.UserFormDto;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @Setter
public class User {

    @Id
    private String id;
    private String password;
    private String name;

    @Builder
    public User(String id, String password, String name) {
        this.id = id;
        this.password = password;
        this.name = name;
    }

    public static User createUser(UserFormDto form, BCryptPasswordEncoder encoder) {
        User user = new User();
        user.setId(form.getId());
        user.setPassword(encoder.encode(form.getPassword()));
        user.setName(form.getName());
        return user;
    }


}
