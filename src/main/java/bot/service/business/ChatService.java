package bot.service.business;

import bot.entity.domain.Stats;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for working with a {@code Chat.java}
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final StatsService statsService;

    /**
     * Get all chat ids that have a bot.
     *
     * @return set chat ids.
     */
    public Set<Long> getAllChatIds() {
        return statsService.getAll().stream()
            .map(Stats::getChatId)
            .collect(Collectors.toSet());
    }

}
