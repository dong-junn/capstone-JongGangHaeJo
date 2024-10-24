package jeiu.capstone.jongGangHaejo.repository;

import jeiu.capstone.jongGangHaejo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepisotory extends JpaRepository<User, Long> {
}
