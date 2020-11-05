package pidarbot.service.business;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pidarbot.entity.enums.CommandBotEnum;
import pidarbot.service.providers.impl.GoodBoyProviders;
import pidarbot.service.providers.impl.PidorProviders;
import pidarbot.service.providers.impl.RegistrationProviders;
import pidarbot.service.providers.impl.StatsProviders;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PidorService {
    private final TelegramBot bot;
    private final RegistrationProviders registrationProviders;
    private final StatsProviders statsProviders;
    private final PidorProviders pidorProviders;
    private final GoodBoyProviders goodBoyProviders;
    protected int offset = 0;

    /* todo[vmurzakov] формирование хешмапы для Providers, пока нет времени допилить это
    private final Map<Enum<CommandBotEnum>, CommandProviders> commandProviders;
    @Autowired
    public PidrService(TelegramBot bot, List<CommandProviders> providers) {
        this.bot = bot;
        this.commandProviders = providers.stream()
                .collect(toMap(CommandProviders::getCommand, Function.identity()));
    }*/

    @Scheduled(cron = "${job.telegram.getting-update.interval}")
    synchronized public void getUpdates() {
        GetUpdates getUpdates = new GetUpdates().limit(100).offset(offset).timeout(0);

        bot.execute(getUpdates, new Callback<>() {
            @Override
            public void onResponse(GetUpdates request, GetUpdatesResponse response) {
                List<Update> updates = response.updates();
                try {
                    if (response.updates() == null) return;

                    for (Update update : updates) {
                        CommandBotEnum command = CommandBotEnum.from(update.message().text());
                        log.info("Обработка сообщения с типом {} в чате {}", command, update.message().chat().id());
                        //todo[vmurzakov]: заменить все if-else на Providers, но нужно вынести домены в абстракцию
                        if (command == CommandBotEnum.REG) {
                            registrationProviders.execute(update.message());
                        }
                        if (command == CommandBotEnum.STATS) {
                            statsProviders.execute(update.message());
                        }
                        if (command == CommandBotEnum.PIDR) {
                            pidorProviders.execute(update.message());
                        }
                        if (command == CommandBotEnum.GOODBOY) {
                            goodBoyProviders.execute(update.message());
                        }
                        if (command == CommandBotEnum.UNKNOWN) {
                            log.warn("Неизвестная команда '{}', не могу её обработать", update.message().text());
                        }
                    }

                    if (!updates.isEmpty()) {
                        offset = updates.get(updates.size() - 1).updateId() + 1;
                    }

                } catch (Exception e) {
                    log.error("Произошла ошибка: ", e);
                    if (!updates.isEmpty()) {
                        offset = updates.get(updates.size() - 1).updateId() + 1;
                    }
                }
            }

            @Override
            public void onFailure(GetUpdates request, IOException e) {
                log.error(e.getMessage());
            }
        });
    }
}
