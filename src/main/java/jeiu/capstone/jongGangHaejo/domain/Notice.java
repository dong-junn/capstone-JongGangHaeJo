package jeiu.capstone.jongGangHaejo.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private String creator;
    
    @ElementCollection
    @CollectionTable(name = "notice_files", joinColumns = @JoinColumn(name = "notice_id"))
    @Column(name = "file_id")
    private List<Long> fileIds = new ArrayList<>();

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    @Column
    private String fileUrl;

    public String getFileUrl() {
        return fileUrl;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = this.createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        this.modifiedAt = LocalDateTime.now();
    }

    @Builder
    public Notice(String title, String content, String creator) {
        this.title = title;
        this.content = content;
        this.creator = creator;
    }

    public void setFileIds(List<Long> fileIds) {
        this.fileIds = fileIds;
    }
}