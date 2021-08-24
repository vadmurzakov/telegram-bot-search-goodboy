package pidarbot.service.providers.impl;

import com.pengrad.telegrambot.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pidarbot.entity.enums.CommandBotEnum;
import pidarbot.service.providers.CommandProvider;

@Slf4j
@Component
public class UnknownProvider implements CommandProvider {
    @Override
    public CommandBotEnum getCommand() {
        return CommandBotEnum.UNKNOWN;
    }

    @Override
    public void execute(Message message) {
        log.warn("Неизвестная команда '{}', не могу её обработать", message.text());
    }
}
