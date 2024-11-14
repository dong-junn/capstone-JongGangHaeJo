package jeiu.capstone.jongGangHaejo.repository.admin.post;

import jeiu.capstone.jongGangHaejo.domain.Post;
import jeiu.capstone.jongGangHaejo.dto.admin.AdminPagingDto;

import java.util.List;

public interface AdminPostRepositoryCustom {

    List<Post> getList(AdminPagingDto postSearch);
}
