package jeiu.capstone.jongGangHaejo.repository.admin.post;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jeiu.capstone.jongGangHaejo.domain.Post;
import jeiu.capstone.jongGangHaejo.dto.paging.PagingDto;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static jeiu.capstone.jongGangHaejo.domain.QPost.post;

@RequiredArgsConstructor
public class AdminPostRepositoryImpl implements AdminPostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Post> getList(PagingDto postSearch) {
        return jpaQueryFactory.selectFrom(post)
                .limit(postSearch.getSize())
                .offset(postSearch.getOffset())
                .orderBy(post.postid.desc())
                .fetch();

    }
}
