package bot.repository;

import bot.entity.domain.Stats;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatsRepository extends JpaRepository<Stats, UUID> {
    Stats findByChatIdAndUserId(Long chatId, UUID userId);
    List<Stats> findAllByChatIdAndActive(Long chatId, boolean active);
}
