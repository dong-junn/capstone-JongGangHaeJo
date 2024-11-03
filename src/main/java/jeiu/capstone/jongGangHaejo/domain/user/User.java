package jeiu.capstone.jongGangHaejo.domain.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @Setter
public class User {

    @Id
    private String id;
    private String password;
    private String name;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public User(String id, String password, String name, Role role) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.role = role;
    }
}
