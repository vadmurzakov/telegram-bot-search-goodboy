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
public class PidorProviders extends AbstractGameProviders {
    private final MessageService messageService;

    @Override
    public CommandBotEnum getCommand() {
        return CommandBotEnum.PIDR;
    }

    @Override
    protected String getFormatMessageService() {
        return messageService.randomPidrMessage();
    }

    @Override
    protected void incrementCount(Stats stats) {
        stats.setCountPidrDay(stats.getCountPidrDay() + 1);
        stats.setLastDayPidr(LocalDate.now());
    }

    @Override
    protected LocalDate getLastDayRunGame(Stats stats) {
        return stats.getLastDayPidr();
    }

    @Override
    protected List<Stats> filterStats(List<Stats> statsList) {
        return statsList.stream()
                .filter(e -> {
                    if (e.getLastDayGoodBoy() == null) {
                        return true;
                    } else {
                        return LocalDate.now().compareTo(e.getLastDayGoodBoy()) != 0;
                    }
                })
                .collect(Collectors.toList());
    }
}
