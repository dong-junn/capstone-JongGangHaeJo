package jeiu.capstone.jongGangHaejo.domain;

import jakarta.persistence.*;
import lombok.Builder;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "users_id")
    private String id;

    @Column(name = "users_password")
    private String password; //비밀번호

    @Builder
    public User(String id, String password) {
        this.id = id;
        this.password = password;
    }

    public User() {} //JPA를 사용할 시에 기본생성자는 필수로 두어야한다(내부적으로 사용)
}
