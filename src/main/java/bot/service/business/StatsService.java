package bot.service.business;

import bot.entity.domain.Stats;
import bot.repository.StatsRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class StatsService {
    private final StatsRepository statsRepository;

    public List<Stats> getAll() {
        return statsRepository.findAll();
    }

    public Optional<Stats> findStat(Long chatId, UUID userId) {
        return Optional.ofNullable(statsRepository.findByChatIdAndUserId(chatId, userId));
    }

    @NotNull
    public Stats getStat(Long chatId, UUID userId) {
        return statsRepository.findByChatIdAndUserId(chatId, userId);
    }

    public List<Stats> findStats(Long chatId) {
        return statsRepository.findAllByChatIdAndActive(chatId, true);
    }

    public Stats save(Stats stats) {
        return statsRepository.save(stats);
    }
}
