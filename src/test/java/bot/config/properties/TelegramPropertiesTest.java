package bot.config.properties;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import bot.BootstrapApplicationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TelegramPropertiesTest extends BootstrapApplicationTest {

    @Autowired
    private TelegramProperties telegramProperties;

    @Test
    void getToken() {
        assertNotNull(telegramProperties);
        assertFalse(telegramProperties.getToken().isEmpty());
    }
}