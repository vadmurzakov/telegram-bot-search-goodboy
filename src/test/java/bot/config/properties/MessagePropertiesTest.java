package bot.config.properties;

import static org.junit.jupiter.api.Assertions.assertTrue;

import bot.BootstrapApplicationTest;
import bot.entity.enums.MessageTemplateEnum;
import bot.service.business.MessageService;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MessagePropertiesTest extends BootstrapApplicationTest {
    @Autowired
    private MessageService service;

    @Test
    void getRoosterRandomMsg() {
        final var message = service.randomMessage(MessageTemplateEnum.ROOSTER);
        assertTrue(StringUtils.isNotEmpty(message));
    }

    @Test
    void getAlreadyStartedRandomMsg() {
        final var message = service.randomMessage(MessageTemplateEnum.ALREADY_STARTED);
        assertTrue(StringUtils.isNotEmpty(message));
    }

    @Test
    void getStatsRandomMsg() {
        final var message = service.randomMessage(MessageTemplateEnum.STATS);
        assertTrue(StringUtils.isNotEmpty(message));
    }

    @Test
    void getNoActivePlayersRandomMsg() {
        final var message = service.randomMessage(MessageTemplateEnum.NO_ACTIVE_PLAYERS);
        assertTrue(StringUtils.isNotEmpty(message));
    }

    @Test
    void getLeaveRandomMsg() {
        final var message = service.randomMessage(MessageTemplateEnum.LEAVE);
        assertTrue(StringUtils.isNotEmpty(message));
    }
}