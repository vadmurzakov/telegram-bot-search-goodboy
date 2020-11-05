package pidarbot.entity.enums;

import liquibase.util.StringUtils;

public enum CommandBotEnum {
    REG,
    PIDR,
    GOODBOY,
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
