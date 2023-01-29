package bot.service.business;

import static java.util.stream.Collectors.toMap;

import bot.entity.enums.CommandBotEnum;
import bot.service.providers.CommandProvider;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Ядро бота, по обработке сообщений бота и выполнения его команд
 */
@Slf4j
@Service
public class BotService {
    private final TelegramBot bot;
    private final Map<Enum<CommandBotEnum>, CommandProvider> commandProvidersMap;

    @Autowired
    public BotService(TelegramBot bot, List<CommandProvider> commandProviders) {
        this.bot = bot;
        this.commandProvidersMap =
            commandProviders.stream().collect(toMap(CommandProvider::getCommand, Function.identity()));
    }

    /**
     * Регистрация слушателя который будет получать обновления с серверов телеграмма.
     */
    @PostConstruct
    public void listener() {
        bot.setUpdatesListener(updates -> {
            try {
                for (Update update : updates) {
                    if (update.message() != null) {
                        CommandBotEnum command = CommandBotEnum.from(update.message().text());
                        commandProvidersMap.get(command).execute(update.message());
                    }
                }
                return UpdatesListener.CONFIRMED_UPDATES_ALL;
            } catch (Exception e) {
                log.error("Неизвестная ошибка: ", e);
                return UpdatesListener.CONFIRMED_UPDATES_ALL;
            }
        }, e -> {
        });
    }
}
