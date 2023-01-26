package bot.service.business;

import static java.util.stream.Collectors.toMap;

import bot.entity.enums.CommandBotEnum;
import bot.service.providers.CommandProvider;
import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Ядро бота, по обработке сообщений бота и выполнения его команд
 */
@Slf4j
@Service
public class BotService {
    private final TelegramBot bot;
    private final Map<Enum<CommandBotEnum>, CommandProvider> commandProvidersMap;
    protected int offset;
    private boolean lock;

    @Autowired
    public BotService(TelegramBot bot, List<CommandProvider> commandProviders) {
        this.offset = 0;
        this.bot = bot;
        this.commandProvidersMap = commandProviders.stream().collect(toMap(CommandProvider::getCommand, Function.identity()));
    }

    /**
     * Джоба которая запускается по крону для получения обновлений с сервера телеграм и обработка этих сообщений.
     */
    @Scheduled(cron = "${job.telegram.getting-update.interval}")
    synchronized public void getUpdates() {
        if (lock) return;

        lock = true;
        GetUpdates getUpdates = new GetUpdates().limit(100).offset(offset).timeout(0);

        bot.execute(getUpdates, new Callback<>() {
            @Override
            public void onResponse(GetUpdates request, GetUpdatesResponse response) {
                List<Update> updates = response.updates();
                try {
                    if (response.updates() == null) return;

                    for (Update update : updates) {
                        CommandBotEnum command = CommandBotEnum.from(update.message().text());
                        Chat chat = update.message().chat();
                        log.info("Обработка сообщения с типом {} в чате {}(id={})", command, chat.title(), chat.id());
                        commandProvidersMap.get(command).execute(update.message());
                    }

                    if (!updates.isEmpty()) {
                        offset = updates.get(updates.size() - 1).updateId() + 1;
                    }

                } catch (Exception e) {
                    log.error("Произошла ошибка: ", e);
                    if (!updates.isEmpty()) {
                        offset = updates.get(updates.size() - 1).updateId() + 1;
                    }
                } finally {
                    lock = false;
                }
            }

            @Override
            public void onFailure(GetUpdates request, IOException e) {
                log.error(e.getMessage());
            }
        });
    }
}
