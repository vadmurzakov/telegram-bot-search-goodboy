package bot.entity.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CommandBotEnumTest {

    @Test
    void getCommandGame() {
        assertEquals(CommandBotEnum.from("/game"), CommandBotEnum.GAME);
    }

    @Test
    void getCommandReg() {
        assertEquals(CommandBotEnum.from("/reg"), CommandBotEnum.REG);
    }

    @Test
    void getCommandStats() {
        assertEquals(CommandBotEnum.from("/stats"), CommandBotEnum.STATS);
    }

    @Test
    void getCommandLeave() {
        assertEquals(CommandBotEnum.from("/leave"), CommandBotEnum.LEAVE);
    }

    @Test
    void getCommandUnknown1() {
        assertEquals(CommandBotEnum.from("просто текст"), CommandBotEnum.UNKNOWN);
    }

    @Test
    void getCommandUnknown2() {
        assertEquals(CommandBotEnum.from(""), CommandBotEnum.UNKNOWN);
    }

    @Test
    void getCommandUnknown3() {
        assertEquals(CommandBotEnum.from(null), CommandBotEnum.UNKNOWN);
    }

}