package bot.service.commands;

import bot.config.client.TelegramBotExecutor;
import bot.entity.enums.MessageTemplateEnum;
import bot.service.business.MessageService;
import bot.service.business.UserService;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Абстрактный провайдер, который будет родителем для все поддерживаемых типов команд.
 */
public abstract class AbstractProvider implements CommandProvider {

    /**
     * Telegram client.
     */
    protected TelegramBotExecutor telegramBot;
    protected UserService userService;
    protected MessageService messageService;

    @Autowired
    protected final void setTelegramBot(TelegramBotExecutor telegramBot) {
        this.telegramBot = telegramBot;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Определить от кого пришло сообщение из чата.
     *
     * @param message сообщение из чата
     * @return true - если его отправил админ бота (не путать с админами каналов)
     */
    protected boolean isAdmin(@NotNull Message message) {
        return message.from().id().equals(telegramBot.properties().getAdminId());
    }

    /**
     * Безопасное представление строки в html-формате.
     * Экранирует символы: {@code <, >, @, "}.
     *
     * @param source исходная строка.
     * @return безопасная для html-строка.
     */
    protected String safetyHtml(String source) {
        return StringEscapeUtils.escapeHtml4(source);
    }

    /**
     * Проверка прав на выполнение команды.
     * Если пользователь не зарегистрирован в боте, команда не обработается.
     *
     * @param message метаданые "откуда", "от кого", "что".
     * @return true - если пользователь зарегистрирован и доступ разрешен.
     */
    protected boolean accessCheck(Message message) {
        final var user = userService.findByUserTelegramId(message.from().id());
        if (user.isEmpty()) {
            final var msg = messageService.randomMessage(MessageTemplateEnum.USER_UNKNOWN);
            final var messageChatId = message.chat().id();
            final var messageReplyId = message.messageId();

            telegramBot.execute(new SendMessage(messageChatId, msg).replyToMessageId(messageReplyId));
        }
        return user.isPresent();
    }

    /**
     * Попытка определить к какому провайдеру относится данное сообщение.
     * Необходимо для реализации сложных логик, когда тяжело идентифицировать к какому провайдеру
     * относится сообщение (например при игре в лотерею пользователь не только запускает лотерею,
     * но и делает ставку).
     *
     * @param message содержащий всю метаинформацию о сообщении из телеграмма.
     * @return true - если сообщение относится к текущему провайдеру (переопределяется в имплементациях),
     * false - дефолтная реализация если иного не требуется.
     */
    @Override
    public boolean defineCommand(@NotNull Message message) {
        return false;
    }
}
