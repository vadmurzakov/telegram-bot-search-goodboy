package bot.service.business;

import bot.entity.domain.Lottery;
import bot.repository.LotteryRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class LotteryService {

    private final LotteryRepository lotteryRepository;

    public Lottery save(Lottery lottery) {
        return lotteryRepository.save(lottery);
    }

    public Lottery update(Lottery lottery) {
        if (lottery.getId() == null) {
            throw new IllegalArgumentException("Lottery#id cannot be null");
        }
        return lotteryRepository.save(lottery);
    }

    public Optional<Lottery> findLottery(Long chatId, UUID userId) {
        return Optional.ofNullable(lotteryRepository.findByChatIdAndUserId(chatId, userId));
    }

    public Lottery getLottery(Long chatId, UUID userId) {
        return lotteryRepository.findByChatIdAndUserId(chatId, userId);
    }

}
