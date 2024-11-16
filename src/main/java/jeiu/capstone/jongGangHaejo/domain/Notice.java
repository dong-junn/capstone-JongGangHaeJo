package jeiu.capstone.jongGangHaejo.domain;

import jakarta.persistence.*;
import jeiu.capstone.jongGangHaejo.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    private String creator = "관리자";

    @Builder
    public Notice(String title, String content) {
        this.title = title;
        this.content = content;
    }
}