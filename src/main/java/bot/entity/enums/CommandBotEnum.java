package bot.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Список поддерживаемых команд для бота.
 */
@Getter
@AllArgsConstructor
public enum CommandBotEnum {
    /**
     * Регистрация игрока в чате.
     */
    REG("/reg"),
    /**
     * Запуск игры.
     */
    GAME("/game"),
    /**
     * Получение общей статистики по игрокам в чате.
     */
    STATS("/stats"),
    /**
     * Получение статистики за последний месяц.
     */
    STATS_MONTH("/stats_month"),
    /**
     * Покинуть игру и не принимать в ней участие.
     */
    LEAVE("/leave"),
    /**
     * Отправка информации по новым фичам во все чаты.
     */
    CHANGELOG("/changelog"),
    /**
     * Запуск лотереи.
     */
    LOTTERY("/lottery"),
    /**
     * Неизвесная команда, ни как не обрабатывается.
     */
    UNKNOWN("/unknown");

    private final String command;
}
