package bot.entity.enums;

import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;

/**
 * Список поддерживаемых команд для бота.
 */
public enum CommandBotEnum {
    /**
     * Регистрация игрока в чате.
     */
    REG,
    /**
     * Запуск игры.
     */
    GAME,
    /**
     * Получение общей статистики по игрокам в чате.
     */
    STATS,
    /**
     * Покинуть игру и не принимать в ней участие.
     */
    LEAVE,
    /**
     * Неизвесная команда, ни как не обрабатывается.
     */
    UNKNOWN;

    public static CommandBotEnum from(String source) {
        if (StringUtils.isEmpty(source)) return UNKNOWN;

        return Arrays.stream(CommandBotEnum.values())
            .filter(e -> source.toUpperCase().contains(e.name()))
            .findFirst()
            .orElse(UNKNOWN);
    }
}
