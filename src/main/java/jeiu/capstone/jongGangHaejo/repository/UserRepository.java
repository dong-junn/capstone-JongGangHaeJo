package jeiu.capstone.jongGangHaejo.repository;

import jeiu.capstone.jongGangHaejo.domain.user.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByNameAndEmail(String name, String email);
    Optional<User> findByIdAndEmail(String id, String email);
    Optional<User> findByEmail(String email);
}
