package bot.service.commands;

import bot.config.client.TelegramBotExecutor;
import com.pengrad.telegrambot.model.Message;
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

    /**
     * Конструктор.
     *
     * @param telegramBot клиент телеграмма через который отправляются все запросы.
     */
    @Autowired
    protected final void setTelegramBot(TelegramBotExecutor telegramBot) {
        this.telegramBot = telegramBot;
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
}
