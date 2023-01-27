package bot.repository;

import bot.entity.domain.Client;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Client, UUID> {
    Client findByUsername(String username);
    Client findByUserTelegramId(Long userId);
    Client findUserById(UUID id);
}
