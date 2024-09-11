package jeiu.capstone.jongGangHaejo.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity //JPA가 관리 하는 Entity임을 명시
@Getter
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    @Builder //Builder패턴 사용
    public Post(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
