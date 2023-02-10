package bot.service.commands.providers;

import bot.entity.domain.Journal;
import bot.entity.enums.CommandBotEnum;
import bot.entity.enums.MessageTemplateEnum;
import bot.service.business.JournalService;
import bot.service.commands.AbstractProvider;
import bot.util.DateTimeUtils;
import bot.util.MessagesUtils;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.Calendar;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * Реализация провайдера для комады /stats_month
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StatsMonthProvider extends AbstractProvider {
    private static final String ARCHI_PIDOR = "Главный архипидор только один {0}, а все остальные лишь его подсосы.";
    private static final String TEMPLATE_LINE_USER = "%d. %s — <em>%d %s</em>\n";
    private final JournalService journalService;

    @Override
    public CommandBotEnum getCommand() {
        return CommandBotEnum.STATS_MONTH;
    }

    /**
     * Получение и вывод статистики за текущий месяц.
     *
     * @param message объект Message в рамках которого пришла команда на исполнение
     *                содержит в себе всю метаинформацию необходимую для выполнения команды
     */
    @Override
    public void execute(@NotNull Message message) {
        final var chatId = message.chat().id();
        final var stats = journalService.findAllByCurrentMonth(chatId);
        final var groupByUserId = stats.stream().collect(Collectors.groupingBy(Journal::getUserId));

        final var currentNumberMonth = Calendar.getInstance(TimeZone.getDefault()).get(Calendar.MONTH);
        final var nameMonth = DateTimeUtils.getNameMonth(currentNumberMonth);
        log.info("Запрос статистики за {} в чате '{}'(idChat={})", nameMonth, message.chat().title(), chatId);

        final var sortGroupByUserId = groupByUserId
            .entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size()))
            .entrySet().stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));


        var index = 0;
        var msg = new StringBuilder(header());
        msg.append(opening()).append("\n");
        for (var item : sortGroupByUserId.entrySet()) {
            index++;
            final var user = userService.findById(item.getKey());
            final var countValue = item.getValue();
            final var countName = MessagesUtils.declensionOfNumbers(countValue, "раз", "раза", "раз");
            msg.append(TEMPLATE_LINE_USER.formatted(index, safetyHtml(user.toString()), countValue, countName));
        }

        final var request = new SendMessage(chatId, msg.toString())
            .parseMode(ParseMode.HTML)
            .replyToMessageId(message.messageId());
        final var execute = telegramBot.execute(request);

        if (!execute.isOk()) {
            log.error("Для команды {} вызов api закончился ошибкой: {}", getCommand(), execute.description());
        }
    }

    private String opening() {
        var header = messageService.randomMessage(MessageTemplateEnum.STATS);
        while (header.equals(ARCHI_PIDOR)) {
            header = messageService.randomMessage(MessageTemplateEnum.STATS);
        }
        return safetyHtml(header);
    }

    private String header() {
        final var currentNumberMonth = Calendar.getInstance(TimeZone.getDefault()).get(Calendar.MONTH);
        final var nameMonth = DateTimeUtils.getNameMonth(currentNumberMonth);
        return "<strong>Топсы за " + nameMonth + "</strong>\n\n";
    }
}
