package bot.config.client;

import bot.config.properties.TelegramProperties;
import com.pengrad.telegrambot.TelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TelegramBotExecutor extends TelegramBot {

    private final TelegramProperties telegramProperties;

    @Autowired
    public TelegramBotExecutor(TelegramProperties telegramProperties) {
        super(telegramProperties.getToken());
        this.telegramProperties = telegramProperties;
    }

    public TelegramProperties properties() {
        return telegramProperties;
    }
}
