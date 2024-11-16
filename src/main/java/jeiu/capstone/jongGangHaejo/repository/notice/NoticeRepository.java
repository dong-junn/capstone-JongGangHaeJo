package jeiu.capstone.jongGangHaejo.repository.notice;

import jeiu.capstone.jongGangHaejo.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long>, NoticeRepositoryCustom {
}
