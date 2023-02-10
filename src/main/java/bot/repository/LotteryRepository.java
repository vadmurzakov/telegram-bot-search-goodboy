package bot.repository;

import bot.entity.domain.Lottery;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LotteryRepository extends JpaRepository<Lottery, UUID> {
    Lottery findByChatIdAndUserId(Long chatId, UUID userId);
}
