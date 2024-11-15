package jeiu.capstone.jongGangHaejo.repository.admin.user;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jeiu.capstone.jongGangHaejo.domain.user.QUser;
import jeiu.capstone.jongGangHaejo.domain.user.User;
import jeiu.capstone.jongGangHaejo.dto.admin.user.UserSearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.util.List;

@RequiredArgsConstructor
public class AdminUserRepositoryCustomImpl implements AdminUserRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<User> findBySearchCondition(UserSearchCondition condition, Pageable pageable) {
        QUser user = QUser.user;

        List<User> content = queryFactory
                .selectFrom(user)
                .where(
                        idContains(condition.getId())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(user.count())
                .from(user)
                .where(
                        idContains(condition.getId())
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression idContains(String id) {
        return StringUtils.hasText(id) ? QUser.user.id.contains(id) : null;
    }
}
