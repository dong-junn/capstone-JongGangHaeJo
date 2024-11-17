package jeiu.capstone.jongGangHaejo.repository.admin.post;

import jeiu.capstone.jongGangHaejo.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminPostRepository extends JpaRepository<Post, Long>, AdminPostRepositoryCustom {
}
