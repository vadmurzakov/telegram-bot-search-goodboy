package bot.service.business;

import bot.entity.domain.Stats;
import bot.repository.StatsRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class StatsService {
    private final StatsRepository statsRepository;

    public Stats findStat(Long chatId, Long userId) {
        return statsRepository.findByChatIdAndUserId(chatId, userId);
    }

    public List<Stats> findStats(Long chatId) {
        return statsRepository.findAllByChatIdAndActive(chatId, true);
    }

    public Stats save(Stats stats) {
        return statsRepository.save(stats);
    }
}
