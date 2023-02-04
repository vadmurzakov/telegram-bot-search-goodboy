package bot.service.business;

import static java.util.stream.Collectors.toMap;

import bot.config.client.TelegramBotExecutor;
import bot.entity.enums.CommandBotEnum;
import bot.service.commands.CommandProvider;
import com.pengrad.telegrambot.UpdatesListener;
import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Ядро бота, по обработке сообщений бота и выполнения его команд
 */
@Slf4j
@Service
public class BotService {
    private final TelegramBotExecutor bot;
    private final Map<Enum<CommandBotEnum>, CommandProvider> commandProvidersMap;

    @Autowired
    public BotService(TelegramBotExecutor bot, List<CommandProvider> commandProviders) {
        this.bot = bot;
        this.commandProvidersMap = commandProviders.stream()
            .collect(toMap(CommandProvider::getCommand, Function.identity()));

    }

    /**
     * Регистрация слушателя который будет получать обновления с серверов телеграмма.
     */
    @PostConstruct
    public void listener() {
        bot.setUpdatesListener(updates -> {
            try {
                for (var update : updates) {
                    // реагируем только на явные текстовые команды в чате, игнорируя другие события
                    if (update.message() != null && update.message().text() != null) {
                        CommandBotEnum command = defineCommand(update.message().text());
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

    /**
     * <p>Логика определения команды не является самой очевидной, т.к. бот перехватывает множество событий:</p>
     * <ul>
     *     <li>Когда к боту обращаются через его @username</li>
     *     <li>Когда к боту обращаются через вызов заранее установленных команд (начинаются со знака "/")</li>
     *     <li>Любые алерты в чате так же провоцируют бота (добавлен участник, изменено фото, закреп сообщений)</li>
     *     <li>Так же явный вызов команды может быть как с указанием username, так и без, например: /reg или /reg@nameBot</li>
     *     <li>Название некоторых команд похожи, из-за чего поиск их через contains становится небезопасным. Пример: /stats и /stats_month</li>
     *     <li>В команду /cangelog может быть передан список новых добавленных команд, что может привести к конфликтам при резолве провайдера</li>
     * </ul>
     *
     * <p>Для того чтобы определение провайдера было корректным мы определили ряд правил по которым определяем провайдера</p>
     * <ul>
     *     <li>Поиск через contains допустим только для провайдера {@code ChangelogProvider.java}, остальные ищутся по полному совпадению команды</li>
     *     <li>/reg и /REG должны быть эквивалентны</li>
     *     <li>/reg и /reg@username_bot должены быть эквивалентны</li>
     *     <li>Если в перехватчике нет сообщения, провайдер будет определен {@code UnknownProvider.java} (справедливо для разных событий типа смены аватарки группы)</li>
     *     <li>Если по каким-то причинам мы не смогли найти подходящий провайдер, будет выбран {@code UnknownProvider.java}</li>
     *     <li>Если в сообщении содержится команда /changelog, она является приоритетной и провайдер будет определен как {@code ChangelogProvider.java}</li>
     * </ul>>
     *
     * @param message текст сообщения который был перехвачен из чата.
     * @return определяется тип команды и возвращается {@link CommandBotEnum}
     */
    protected CommandBotEnum defineCommand(String message) {
        if (StringUtils.isEmpty(message)) {
            return CommandBotEnum.UNKNOWN;
        }

        if (message.toUpperCase().contains(CommandBotEnum.CHANGELOG.name())) {
            return CommandBotEnum.CHANGELOG;
        }

        return Arrays.stream(CommandBotEnum.values())
            .filter(e -> message.equalsIgnoreCase(e.getCommand()) ||
                         message.equalsIgnoreCase(e.getCommand() + bot.properties().getUsername()))
            .findFirst()
            .orElse(CommandBotEnum.UNKNOWN);
    }
}
