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
    public void getRoosterRandomMsg() {
        final var randomMessage = service.randomMessage(MessageTemplateEnum.ROOSTER);
        assertTrue(StringUtils.isNotEmpty(randomMessage));
    }

    @Test
    public void getAlreadyStartedRandomMsg() {
        final var randomMessage = service.randomMessage(MessageTemplateEnum.ALREADY_STARTED);
        assertTrue(StringUtils.isNotEmpty(randomMessage));
    }

    @Test
    public void getStatsRandomMsg() {
        final var randomMessage = service.randomMessage(MessageTemplateEnum.STATS);
        assertTrue(StringUtils.isNotEmpty(randomMessage));
    }

    @Test
    public void getNoActivePlayersRandomMsg() {
        final var randomStatsMessage = service.randomMessage(MessageTemplateEnum.NO_ACTIVE_PLAYERS);
        assertTrue(StringUtils.isNotEmpty(randomStatsMessage));
    }
}