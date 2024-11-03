package jeiu.capstone.jongGangHaejo.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;  // Primary Key

    // Post 설정
//    @Setter
//    @ManyToOne(fetch = FetchType.LAZY)  // Post와 다대일 관계
//    @JoinColumn(name = "post_id")
//    private Post post;  // 게시물과의 연관관계

    @Column(nullable = false, length = 255)
    private String fileName;  // 원래 파일 이름

    @Column(nullable = false, length = 1024)
    private String s3Path;  // S3에 저장된 파일 경로(URL)

    @Column(length = 50)
    private String fileType;  // 파일 유형 (예: 이미지, 문서 등)

    @Column(name = "file_size")
    private Long fileSize;  // 파일 크기 (KB 또는 MB)

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();  // 파일 업로드 시간

    // 빌더 패턴을 사용한 생성자
    @Builder
    public File(String fileName, String s3Path, String fileType, Long fileSize) {
        this.fileName = fileName;
        this.s3Path = s3Path;
        this.fileType = fileType;
        this.fileSize = fileSize;
    }

    // 기타 메서드들 추가 가능 (예: 업데이트 시 메서드 등)
}
