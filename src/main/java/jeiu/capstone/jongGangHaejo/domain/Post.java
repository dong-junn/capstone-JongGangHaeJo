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

    //CascadeType.REMOVE vs orphanRemoval = true 의 차이
    //전자는 게시물에서 등록된 파일을 제거해도(연관 관계 삭제) 해당 파일은 그대로 DB에 남아있게 됨. (고아)
    //후자는 파일을 제거하면, 해당 파일도 DB에서 제거되게 됨.
    @ElementCollection
    @CollectionTable(name = "post_file_ids", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "file_id")
    private List<Long> fileIds = new ArrayList<>();  // 게시물에 첨부된 파일들

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
    public Post(Long postid, String username, String title, String content, String team, String youtubelink, List<Long> fileIds) {
        this.postid = postid;
        this.username = username;
        this.title = title;
        this.content = content;
        this.team = team;
        this.youtubelink = youtubelink;
        if (fileIds != null) {
            this.fileIds = fileIds;
        }
    }

    // 파일 추가 메서드 (양방향 연관 관계 설정)
    public void addFile(Long fileId) {
        this.fileIds.add(fileId);  // 파일에 게시물(Post) 설정
    }
}
