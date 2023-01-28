package bot.repository;

import bot.entity.domain.User;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    User findByUsername(String username);
    User findByUserTelegramId(Long userTelegramId);
    User findUserById(UUID id);
}
