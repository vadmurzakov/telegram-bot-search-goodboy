package pidarbot.config.properties;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class TelegramPropertiesTest {

    @Autowired
    private TelegramProperties telegramProperties;

    @Test
    void getToken() {
        assertNotNull(telegramProperties);
        assertFalse(telegramProperties.getToken().isEmpty());
    }
}