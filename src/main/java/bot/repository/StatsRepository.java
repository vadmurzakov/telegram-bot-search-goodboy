package bot.repository;

import bot.entity.domain.Stats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Stats, Long> {
    Stats findByChatIdAndUserId(Long chatId, Long userId);
    List<Stats> findAllByChatIdAndActive(Long chatId, boolean active);
}
