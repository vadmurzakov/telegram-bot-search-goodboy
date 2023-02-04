package bot.service.commands.providers;

import bot.entity.domain.Stats;
import bot.entity.enums.CommandBotEnum;
import bot.entity.enums.MessageTemplateEnum;
import bot.service.business.MessageService;
import bot.service.business.StatsService;
import bot.service.business.UserService;
import bot.service.commands.AbstractProvider;
import bot.util.MessagesUtils;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import java.text.MessageFormat;
import java.util.Comparator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Реализация провайдера для комады /stats
 */
@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class StatsProvider extends AbstractProvider {
    private static final String ARCHI_PIDOR = "Главный архипидор только один {0}, а все остальные лишь его подсосы.";
    private static final String TEMPLATE_LINE_USER = "%d. %s — <em>%d %s</em>\n";
    private final StatsService statsService;
    private final UserService userService;
    private final MessageService messageService;

    @Override
    public CommandBotEnum getCommand() {
        return CommandBotEnum.STATS;
    }

    /**
     * Получение и вывод статистики за все время.
     *
     * @param message объект Message в рамках которого пришла команда на исполнение
     *                содержит в себе всю метаинформацию необходимую для выполнения команды
     */
    public void execute(@NotNull Message message) {
        final var chatId = message.chat().id();
        final var opening = safetyHtml(messageService.randomMessage(MessageTemplateEnum.STATS));

        log.info("Запрос статистики в чате '{}'(idChat={})", message.chat().title(), message.chat().id());

        var statsList = statsService.findStats(chatId).stream()
            .sorted(Comparator.comparingLong(Stats::getCountRooster).reversed())
            .toList();

        var msg = new StringBuilder();
        if (ARCHI_PIDOR.equals(opening)) {
            var stats = statsList.get(0);
            var user = userService.findById(stats.getUserId());
            msg.append(MessageFormat.format(opening, safetyHtml(user.toString())));
        } else {
            msg.append(header());
            msg.append(opening).append("\n");
            for (int i = 0; i < statsList.size(); i++) {
                var stats = statsList.get(i);
                var user = userService.findById(stats.getUserId());
                final var countValue = stats.getCountRooster().intValue();
                final var countName = MessagesUtils.declensionOfNumbers(countValue, "раз", "раза", "раз");
                msg.append(TEMPLATE_LINE_USER.formatted(i + 1, safetyHtml(user.toString()), countValue, countName));
            }
        }

        final var request = new SendMessage(chatId, msg.toString())
            .parseMode(ParseMode.HTML)
            .replyToMessageId(message.messageId());
        final var execute = telegramBot.execute(request);
        if (!execute.isOk()) {
            log.error("Для команды {} вызов api.telegram.org закончился ошибкой: {}", getCommand(), execute.description());
        }
    }

    private String header() {
        return "<strong>Топсы за все время</strong>\n\n";
    }
}
