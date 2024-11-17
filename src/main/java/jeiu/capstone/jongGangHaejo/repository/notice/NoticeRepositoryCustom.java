package jeiu.capstone.jongGangHaejo.repository.notice;

import jeiu.capstone.jongGangHaejo.domain.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoticeRepositoryCustom {

    Page<Notice> findAllWithPaging(Pageable pageable);

}
