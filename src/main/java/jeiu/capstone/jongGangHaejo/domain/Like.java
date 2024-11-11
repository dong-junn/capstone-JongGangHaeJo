package jeiu.capstone.jongGangHaejo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "likes")
@Getter
@Setter
@NoArgsConstructor
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long postId;

    @Column(nullable = false)
    private String username;

    public Like(Long postId, String username) {
        this.postId = postId;
        this.username = username;
    }
}
