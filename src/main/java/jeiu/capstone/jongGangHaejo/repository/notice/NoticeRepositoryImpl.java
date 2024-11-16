package jeiu.capstone.jongGangHaejo.repository.notice;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jeiu.capstone.jongGangHaejo.domain.Notice;
import jeiu.capstone.jongGangHaejo.domain.QNotice;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class NoticeRepositoryImpl implements NoticeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Notice> findAllWithPaging(Pageable pageable) {
        QNotice notice = QNotice.notice;

        List<Notice> content = queryFactory
                .selectFrom(notice)
                .orderBy(notice.id.desc())  // id 기준 내림차순 정렬
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(notice.count())
                .from(notice)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}