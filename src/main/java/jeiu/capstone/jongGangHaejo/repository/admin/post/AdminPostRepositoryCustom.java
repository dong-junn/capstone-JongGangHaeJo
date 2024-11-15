package jeiu.capstone.jongGangHaejo.repository.admin.post;

import jeiu.capstone.jongGangHaejo.domain.Post;
import jeiu.capstone.jongGangHaejo.dto.paging.PagingDto;

import java.util.List;

public interface AdminPostRepositoryCustom {

    List<Post> getList(PagingDto postSearch);
}
