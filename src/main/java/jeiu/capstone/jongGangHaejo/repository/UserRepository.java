package jeiu.capstone.jongGangHaejo.repository;

import jeiu.capstone.jongGangHaejo.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
