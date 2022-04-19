package bot.service.providers;

import bot.entity.enums.CommandBotEnum;
import com.pengrad.telegrambot.model.Message;

/**
 * Интерфейс для реализации бизнес-логики выполнения команд бота
 */
public interface CommandProvider {
    /**
     * Возвращает команду, которую обрабатывает реализация
     * @return UNKNOWN - если команда неизвестная, CommandBotEnum - в противном случае
     */
    CommandBotEnum getCommand();

    /**
     * Выполняет бизнес логику команды бота
     * @param message - объект Message в рамках которого пришла команда на исполнение
     *                содержит в себе всю метаинформацию необходимую для выполнения команды
     */
    void execute(Message message);
}
