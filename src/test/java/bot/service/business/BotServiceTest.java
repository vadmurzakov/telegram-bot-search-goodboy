package bot.service.business;

import static org.junit.jupiter.api.Assertions.assertEquals;

import bot.BootstrapApplicationTest;
import bot.entity.enums.CommandBotEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class BotServiceTest extends BootstrapApplicationTest {

    @Autowired
    private BotService botService;

    @Test
    void defineCommand_0() {
        var command = botService.defineCommand("/reg");
        assertEquals(command, CommandBotEnum.REG);
    }

    @Test
    void defineCommand_1() {
        var command = botService.defineCommand("/REG");
        assertEquals(command, CommandBotEnum.REG);
    }

    @Test
    void defineCommand_2() {
        var command = botService.defineCommand("/reg@username_bot");
        assertEquals(command, CommandBotEnum.REG);
    }

    @Test
    void defineCommand_3() {
        var command = botService.defineCommand("/stat");
        assertEquals(command, CommandBotEnum.UNKNOWN);
    }

    @Test
    void defineCommand_4() {
        var command = botService.defineCommand("/stats");
        assertEquals(command, CommandBotEnum.STATS);
    }

    @Test
    void defineCommand_5() {
        var command = botService.defineCommand("stats");
        assertEquals(command, CommandBotEnum.UNKNOWN);
    }

    @Test
    void defineCommand_6() {
        var command = botService.defineCommand("/changelog новая фича /stats");
        assertEquals(command, CommandBotEnum.CHANGELOG);
    }

    @Test
    void defineCommand_7() {
        var command = botService.defineCommand("");
        assertEquals(command, CommandBotEnum.UNKNOWN);
    }

    @Test
    void defineCommand_8() {
        var command = botService.defineCommand("123");
        assertEquals(command, CommandBotEnum.UNKNOWN);
    }
}