package jeiu.capstone.jongGangHaejo.domain;

import jakarta.persistence.*;
import lombok.Builder;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "users_id")
    private Long id; //아이디

    @Column(name = "users_username")
    private String username; //이름

    @Column(name = "users_email")
    private String email; //이메일

    @Column(name = "users_password")
    private String password; //비밀번호
}
