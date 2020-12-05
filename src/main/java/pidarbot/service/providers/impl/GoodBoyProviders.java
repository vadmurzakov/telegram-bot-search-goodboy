package pidarbot.service.providers.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pidarbot.entity.domain.Stats;
import pidarbot.entity.enums.CommandBotEnum;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Murzakov Vadim <murzakov.vadim@otr.ru>
 */
@Component
@Transactional
@RequiredArgsConstructor
public class GoodBoyProviders extends AbstractGameProviders {
    private final MessageService messageService;

    @Override
    public CommandBotEnum getCommand() {
        return CommandBotEnum.GOODBOY;
    }

    @Override
    protected String getFormatMessageService() {
        return messageService.randomGoodBoyMessage();
    }

    @Override
    protected void incrementCount(Stats stats) {
        stats.setCountGoodBoy(stats.getCountGoodBoy() + 1);
        stats.setLastDayGoodBoy(LocalDate.now());
    }

    @Override
    protected LocalDate getLastDayRunGame(Stats stats) {
        return stats.getLastDayGoodBoy();
    }

    @Override
    protected List<Stats> filterStats(List<Stats> statsList) {
        return statsList.stream()
                .filter(e -> {
                    if (e.getLastDayPidr() == null) {
                        return true;
                    } else {
                        return LocalDate.now().compareTo(e.getLastDayPidr()) != 0;
                    }
                })
                .collect(Collectors.toList());
    }
}
