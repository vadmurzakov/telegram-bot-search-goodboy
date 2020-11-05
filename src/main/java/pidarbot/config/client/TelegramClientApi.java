package pidarbot.config.client;

import com.pengrad.telegrambot.TelegramBot;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pidarbot.config.properties.TelegramProperties;

/**
 * @author Murzakov Vadim <murzakov.vadim@otr.ru>
 */
@Configuration
@RequiredArgsConstructor
public class TelegramClientApi {

    private final TelegramProperties properties;

    @Bean
    public TelegramBot bot() {
        return new TelegramBot(properties.getToken());
    }
}
