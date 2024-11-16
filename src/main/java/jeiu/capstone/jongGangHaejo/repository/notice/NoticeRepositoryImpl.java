package jeiu.capstone.jongGangHaejo.repository.notice;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
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

        JPAQuery<Notice> query = queryFactory
                .selectFrom(notice);

        // Pageable의 정렬 조건 적용
        pageable.getSort().forEach(order -> {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            PathBuilder orderByExpression = new PathBuilder(Notice.class, "notice");
            query.orderBy(new OrderSpecifier(direction, orderByExpression.get(order.getProperty())));
        });

        List<Notice> content = query
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