package pidarbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pidarbot.entity.domain.Stats;

import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Stats, Long> {
    Stats findByChatIdAndUserId(Long chatId, Long userId);
    List<Stats> findAllByChatIdAndActive(Long chatId, boolean active);
}
