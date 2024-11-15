package jeiu.capstone.jongGangHaejo.repository.admin.user;

import jeiu.capstone.jongGangHaejo.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminUserRepository extends JpaRepository<User, String> , AdminUserRepositoryCustom {
}
