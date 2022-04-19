package bot.config.client;

import bot.config.properties.TelegramProperties;
import com.pengrad.telegrambot.TelegramBot;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class TelegramClientApi {

    private final TelegramProperties properties;

    @Bean
    public TelegramBot bot() {
        return new TelegramBot(properties.getToken());
    }
}
