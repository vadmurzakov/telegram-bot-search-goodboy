package bot.service.commands.providers;

import bot.entity.enums.CommandBotEnum;
import bot.service.business.ChatService;
import bot.service.commands.AbstractProvider;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.Set;
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
        return CommandBotEnum.CHANGELOG;
    }

    /**
     * Отправка во все чаты уведомлений о новых фичах.
     *
     * @param message объект Message в рамках которого пришла команда на исполнение
     *                содержит в себе всю метаинформацию необходимую для выполнения команды
     */
    @Override
    public void execute(@NotNull Message message) {
        try {
            if (permissionCheck(message)) {
                Set<Long> chatIds = chatService.getAllChatIds();
                log.debug("Список чатов где есть бот: " + chatIds);

                var changeLog = message.text().replace("/changelog", StringUtils.EMPTY).trim();

                if (StringUtils.isNotEmpty(changeLog)) {
                    var successfulSending = 0;
                    for (var chatId : chatIds) {
                        var request = new SendMessage(chatId, changeLog);
                        var execute = telegramBot.execute(request);
                        if (execute.isOk()) {
                            successfulSending++;
                        }
                    }
                    log.info("Changelog отправен в {} чатов из {}", successfulSending, chatIds.size());
                }
            }
        } catch (Exception e) {
            log.error("Не удалось отправить changelog: " + e.getMessage());
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
}
