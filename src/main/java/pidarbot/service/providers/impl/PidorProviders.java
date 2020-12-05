package pidarbot.service.providers.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pidarbot.entity.domain.Stats;
import pidarbot.entity.enums.CommandBotEnum;

import java.time.LocalDate;

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
}
