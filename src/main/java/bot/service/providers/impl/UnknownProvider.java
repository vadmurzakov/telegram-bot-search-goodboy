package bot.service.providers.impl;

import bot.entity.enums.CommandBotEnum;
import bot.service.providers.CommandProvider;
import com.pengrad.telegrambot.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
