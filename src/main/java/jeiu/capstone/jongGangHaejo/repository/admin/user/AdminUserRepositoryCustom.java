package jeiu.capstone.jongGangHaejo.repository.admin.user;

import jeiu.capstone.jongGangHaejo.domain.user.User;
import jeiu.capstone.jongGangHaejo.dto.admin.user.UserSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminUserRepositoryCustom {

    Page<User> findBySearchCondition(UserSearchCondition condition, Pageable pageable);

}
