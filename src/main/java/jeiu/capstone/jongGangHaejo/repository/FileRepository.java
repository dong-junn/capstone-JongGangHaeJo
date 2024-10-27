package jeiu.capstone.jongGangHaejo.repository;

import jeiu.capstone.jongGangHaejo.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {
    // 특정 게시물에 연결된 모든 파일을 조회하는 메서드
    List<File> findByFileIdIn(List<Long> fileIds);
}
