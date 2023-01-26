package bot.config.properties;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import bot.service.business.MessageService;
import liquibase.util.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MessagePropertiesTest {

    @Autowired
    private MessageProperties properties;
    @Autowired
    private MessageService service;

    @Test
    public void getRooster() {
        assertNotNull(properties);
        assertNotNull(properties.getRooster());
        assertFalse(properties.getRooster().isEmpty());
    }

    @Test
    public void getAlreadyStarted() {
        assertNotNull(properties);
        assertNotNull(properties.getAlreadyStarted());
        assertFalse(properties.getAlreadyStarted().isEmpty());
    }

    @Test
    public void getStats() {
        assertNotNull(properties);
        assertNotNull(properties.getStats());
        assertFalse(properties.getStats().isEmpty());
    }

    @Test
    public void getRoosterRandomMsg() {
        String randomRoosterMessage = service.randomRoosterMessage();
        assertTrue(StringUtils.isNotEmpty(randomRoosterMessage));
    }

    @Test
    public void getAlreadyStartedRandomMsg() {
        String randomGoodBoyMessage = service.randomAlreadyStartedMessage();
        assertTrue(StringUtils.isNotEmpty(randomGoodBoyMessage));
    }

    @Test
    public void getStatsRandomMsg() {
        String randomStatsMessage = service.randomStatsMessage();
        assertTrue(StringUtils.isNotEmpty(randomStatsMessage));
    }
}