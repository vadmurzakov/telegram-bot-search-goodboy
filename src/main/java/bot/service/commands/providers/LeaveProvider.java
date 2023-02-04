package bot.service.commands.providers;

import bot.entity.enums.CommandBotEnum;
import bot.entity.enums.MessageTemplateEnum;
import bot.service.business.MessageService;
import bot.service.commands.AbstractProvider;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * Реализация провайдера для команды /leave
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LeaveProvider extends AbstractProvider {
    private final MessageService messageService;

    @Override
    public CommandBotEnum getCommand() {
        return CommandBotEnum.LEAVE;
    }

    /**
     * Выйти из игры.
     *
     * @param message объект Message в рамках которого пришла команда на исполнение
     *                содержит в себе всю метаинформацию необходимую для выполнения команды
     */
    @Override
    public void execute(@NotNull Message message) {
        final var chatId = message.chat().id();

        final var msg = messageService.randomMessage(MessageTemplateEnum.LEAVE);
        final var request = new SendMessage(chatId, msg).replyToMessageId(message.messageId());
        telegramBot.execute(request);
    }
}
