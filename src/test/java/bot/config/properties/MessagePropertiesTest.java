package bot.config.properties;

import bot.service.business.MessageService;
import liquibase.util.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    public void getGoodBoy() {
        assertNotNull(properties);
        assertNotNull(properties.getGoodBoy());
        assertFalse(properties.getGoodBoy().isEmpty());
    }

    @Test
    public void getRoosterRandomMsg() {
        String randomRoosterMessage = service.randomRoosterMessage();
        assertTrue(StringUtils.isNotEmpty(randomRoosterMessage));
    }

    @Test
    public void getGoodBoyRandomMsg() {
        String randomGoodBoyMessage = service.randomGoodBoyMessage();
        assertTrue(StringUtils.isNotEmpty(randomGoodBoyMessage));
    }
}