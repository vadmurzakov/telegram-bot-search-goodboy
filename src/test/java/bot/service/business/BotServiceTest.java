package bot.service.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import bot.BootstrapApplicationTest;
import bot.entity.enums.CommandBotEnum;
import com.pengrad.telegrambot.model.Message;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

class BotServiceTest extends BootstrapApplicationTest {

    @Autowired
    private BotService botService;
    @Mock
    private Message message = new Message();

    @Test
    void defineCommand_0() {
        when(message.text()).thenReturn("/reg");
        var command = botService.defineCommand(message);
        assertEquals(command, CommandBotEnum.REG);
    }

    @Test
    void defineCommand_1() {
        when(message.text()).thenReturn("/REG");
        var command = botService.defineCommand(message);
        assertEquals(command, CommandBotEnum.REG);
    }

    @Test
    void defineCommand_2() {
        when(message.text()).thenReturn("/reg@username_bot");
        var command = botService.defineCommand(message);
        assertEquals(command, CommandBotEnum.REG);
    }

    @Test
    void defineCommand_3() {
        when(message.text()).thenReturn("/stat");
        var command = botService.defineCommand(message);
        assertEquals(command, CommandBotEnum.UNKNOWN);
    }

    @Test
    void defineCommand_4() {
        when(message.text()).thenReturn("/stats");
        var command = botService.defineCommand(message);
        assertEquals(command, CommandBotEnum.STATS);
    }

    @Test
    void defineCommand_5() {
        when(message.text()).thenReturn("stats");
        var command = botService.defineCommand(message);
        assertEquals(command, CommandBotEnum.UNKNOWN);
    }

    @Test
    void defineCommand_6() {
        when(message.text()).thenReturn("/changelog новая фича /stats");
        var command = botService.defineCommand(message);
        assertEquals(command, CommandBotEnum.CHANGELOG);
    }

    @Test
    void defineCommand_7() {
        when(message.text()).thenReturn("");
        var command = botService.defineCommand(message);
        assertEquals(command, CommandBotEnum.UNKNOWN);
    }

    @Test
    void defineCommand_8() {
        when(message.text()).thenReturn("/123");
        var command = botService.defineCommand(message);
        assertEquals(command, CommandBotEnum.UNKNOWN);
    }
}