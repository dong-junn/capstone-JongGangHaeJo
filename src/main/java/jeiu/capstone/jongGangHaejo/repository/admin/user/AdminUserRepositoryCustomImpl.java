package jeiu.capstone.jongGangHaejo.repository.admin.user;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jeiu.capstone.jongGangHaejo.domain.user.QUser;
import jeiu.capstone.jongGangHaejo.domain.user.User;
import jeiu.capstone.jongGangHaejo.dto.admin.user.UserSearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class AdminUserRepositoryCustomImpl implements AdminUserRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<User> findBySearchCondition(UserSearchCondition condition, Pageable pageable) {
        QUser user = QUser.user;

        // 검색 조건 적용
        BooleanExpression searchCondition = createSearchCondition(condition);

        // 페이징된 User 목록 조회
        List<User> content = queryFactory
                .selectFrom(user)
                .leftJoin(user.roles).fetchJoin()
                .where(searchCondition)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(user.id.asc())
                .fetch();

        // 전체 개수 조회
        Long total = queryFactory
                .select(user.count())
                .from(user)
                .where(searchCondition)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    private BooleanExpression createSearchCondition(UserSearchCondition condition) {
        QUser user = QUser.user;
        BooleanExpression expression = null;

        // ID 검색 조건
        if (condition != null && condition.getId() != null && !condition.getId().trim().isEmpty()) {
            expression = user.id.containsIgnoreCase(condition.getId().trim());
        }

        return expression;
    }
}