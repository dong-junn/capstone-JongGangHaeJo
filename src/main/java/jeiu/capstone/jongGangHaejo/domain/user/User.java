package jeiu.capstone.jongGangHaejo.domain.user;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @Setter
public class User {

    @Id
    private String id;
    private String password;
    
    @Column(unique = true)
    private String name;
    
    @Column(unique = true)
    private String email;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    @Builder
    public User(String id, String password, String name, String email, Set<Role> roles) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.email = email;
        this.roles = roles;
    }
}
