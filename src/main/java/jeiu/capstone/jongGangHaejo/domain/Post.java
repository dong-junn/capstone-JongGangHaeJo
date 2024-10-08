package jeiu.capstone.jongGangHaejo.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity //JPA가 관리 하는 Entity임을 명시
@Getter
@Setter
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postid; //Primary Key, 게시물 고유 ID

    private String username;

    @Column(length = 255)
    private String title; //게시물 제목

    @Column(length = 255)
    private String team; //팀원 정보

    @Column(columnDefinition = "TEXT")
    private String content; //게시물 내용

    @Column(name = "youtube_link", length = 255)
    private String youtubelink; // 유튜브 링크

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<File> files = new ArrayList<>();;  // 게시물에 첨부된 파일들

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 생성일시

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now(); // 수정일시

    // 수정 시 updatedAt 필드 자동 갱신
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Builder //Builder패턴 사용
    public Post(String username, String title, String content, String team, String youtubelink, List<File> files) {
        this.username = username;
        this.title = title;
        this.content = content;
        this.team = team;
        this.youtubelink = youtubelink;
        this.files = files != null ? files : new ArrayList<>();
    }

    // 파일 추가 메서드 (양방향 연관 관계 설정)
    public void addFile(File file) {
        files.add(file);
        file.setPost(this);  // 파일에 게시물(Post) 설정
    }
}
