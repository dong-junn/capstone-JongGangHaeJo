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

        // 숫자 부분만 추출하여 정수로 변환
        NumberExpression<Integer> numericPart = Expressions.numberTemplate(Integer.class,
                "CAST(SUBSTRING({0}, LOCATE('user', {0}) + 4) AS INTEGER)", user.id);

        // 페이징된 User ID 목록 조회
        List<String> ids = queryFactory
                .select(user.id)
                .from(user)
                .where(idContains(condition.getId()))
                .orderBy(getOrderSpecifiersWithNumericSort(pageable, numericPart))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (ids.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        // ID 목록으로 User 엔티티와 연관된 데이터 조회
        List<User> content = queryFactory
                .selectFrom(user)
                .leftJoin(user.roles).fetchJoin()
                .where(user.id.in(ids))
                .orderBy(getOrderSpecifiersWithNumericSort(pageable, numericPart))
                .fetch();

        // 전체 개수 조회
        Long total = queryFactory
                .select(user.count())
                .from(user)
                .where(idContains(condition.getId()))
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    private OrderSpecifier<?>[] getOrderSpecifiersWithNumericSort(Pageable pageable, NumberExpression<Integer> numericPart) {
        QUser user = QUser.user;
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if (pageable.getSort().isEmpty()) {
            orders.add(new OrderSpecifier<>(Order.ASC, numericPart));
        } else {
            for (Sort.Order order : pageable.getSort()) {
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;

                switch (order.getProperty()) {
                    case "id":
                        orders.add(new OrderSpecifier<>(direction, numericPart));
                        break;
                    case "name":
                        orders.add(new OrderSpecifier<>(direction, user.name));
                        break;
                    default:
                        orders.add(new OrderSpecifier<>(Order.ASC, numericPart));
                        break;
                }
            }
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression idContains(String id) {
        return StringUtils.hasText(id) ? QUser.user.id.contains(id) : null;
    }
}