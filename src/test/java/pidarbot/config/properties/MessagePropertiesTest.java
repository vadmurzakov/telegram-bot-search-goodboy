package pidarbot.config.properties;

import liquibase.util.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pidarbot.service.business.MessageService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MessagePropertiesTest {

    @Autowired
    private MessageProperties properties;
    @Autowired
    private MessageService service;

    @Test
    public void getPidr() {
        assertNotNull(properties);
        assertNotNull(properties.getPidr());
        assertFalse(properties.getPidr().isEmpty());
    }

    @Test
    public void getPidrRandomMsg() {
        String randomPidrMessage = service.randomPidrMessage();
        assertTrue(StringUtils.isNotEmpty(randomPidrMessage));
    }
}