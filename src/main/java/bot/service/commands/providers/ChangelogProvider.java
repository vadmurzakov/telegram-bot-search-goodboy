package bot.service.commands.providers;

import static bot.entity.enums.CommandBotEnum.CHANGELOG;

import bot.entity.enums.CommandBotEnum;
import bot.service.business.ChatService;
import bot.service.commands.AbstractProvider;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * Реализация провайдера для комады /changelog
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChangelogProvider extends AbstractProvider {
    private final ChatService chatService;

    @Override
    public CommandBotEnum getCommand() {
        return CHANGELOG;
    }

    /**
     * Отправка во все чаты уведомлений о новых фичах.
     *
     * @param message объект Message в рамках которого пришла команда на исполнение
     *                содержит в себе всю метаинформацию необходимую для выполнения команды
     */
    @Override
    public void execute(@NotNull Message message) {
        if (permissionCheck(message)) {
            final var chatIds = chatService.getAllChatIds();
            final var isPhoto = message.photo() != null && StringUtils.isNotEmpty(message.caption());

            var successfulSending = 0;
            for (var chatId : chatIds) {
                try {
                    final var execute = sendChangeLog(message, chatId, isPhoto);
                    if (execute.isOk()) {
                        successfulSending++;
                    }
                } catch (Exception e) {
                    log.error("Ошибка отправка changelog в чат {}: {}", chatId, e.getMessage());
                }
            }
            log.info("Changelog отправлен в {} чатов из {}", successfulSending, chatIds.size());

        }
    }

    private SendResponse sendChangeLog(Message message, Long chatId, boolean isPhoto) throws Exception {
        if (isPhoto) {
            final var fileUniqueId = message.photo()[message.photo().length - 1].fileId();
            final var changeLog = message.caption().replace(CHANGELOG.getCommand(), StringUtils.EMPTY).trim();
            final var sendPhoto = new SendPhoto(chatId, fileUniqueId).caption(changeLog);
            return telegramBot.execute(sendPhoto);
        } else {
            var changeLog = message.text().replace(CHANGELOG.getCommand(), StringUtils.EMPTY).trim();
            var sendMessage = new SendMessage(chatId, changeLog);
            return telegramBot.execute(sendMessage);
        }
    }

    /**
     * Проверка прав на отправку Changelog. Отправка возможна если:
     * 1. Чейнджлог прислан в личку боту.
     * 2. Чейнджлог отправлен админов бота.
     *
     * @param message откуда пришел чейнджлог.
     * @return true - если все проверки прошли успешно.
     */
    private boolean permissionCheck(Message message) {
        boolean isAdmin = isAdmin(message);
        return message.chat().type() == Chat.Type.Private && isAdmin;
    }

    /**
     * {@inheritDoc}.
     *
     * @param message содержащий всю метаинформацию о сообщении из телеграмма.
     * @return true если это CHANGELOG.
     */
    @Override
    public boolean defineCommand(@NotNull Message message) {
        var text = message.text();
        var isPhoto = message.photo() != null && StringUtils.isNotEmpty(message.caption());

        if (StringUtils.isEmpty(text) && isPhoto) {
            if (message.caption().contains(CommandBotEnum.CHANGELOG.getCommand())) {
                return true;
            }
        }

        return text.toUpperCase().contains(CommandBotEnum.CHANGELOG.name());
    }
}
