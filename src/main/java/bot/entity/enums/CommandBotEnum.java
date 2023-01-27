package bot.entity.enums;

import org.apache.commons.lang3.StringUtils;

public enum CommandBotEnum {
    REG,
    GAME,
    STATS,
    LEAVE,
    UNKNOWN;

    public static CommandBotEnum from(String source) {
        if (StringUtils.isEmpty(source)) return UNKNOWN;

        for(CommandBotEnum commandBotEnum : CommandBotEnum.values()) {
            if (source.toUpperCase().contains(commandBotEnum.name()))
                return commandBotEnum;
        }

        return UNKNOWN;
    }
}
