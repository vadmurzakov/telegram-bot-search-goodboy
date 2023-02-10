package bot.service.commands.providers;

import bot.entity.enums.CommandBotEnum;
import bot.service.commands.AbstractProvider;
import com.pengrad.telegrambot.model.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * Реализация провайдера для неизвестных команд
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UnknownProvider extends AbstractProvider {

    @Override
    public CommandBotEnum getCommand() {
        return CommandBotEnum.UNKNOWN;
    }

    @Override
    public void execute(@NotNull Message message) {
        Long chatId = message.chat().id();
        String chatTitle = message.chat().title();
        log.warn("В чате '{}'(id={}), неизвестная команда '{}' от {}", chatTitle, chatId, message.text(), message.from().firstName());
    }

    @Override
    public boolean defineCommand(@NotNull Message message) {
        return StringUtils.isEmpty(message.text());
    }
}
