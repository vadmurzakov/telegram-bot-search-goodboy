package bot.service.commands.providers;

import bot.entity.domain.Stats;
import bot.entity.enums.CommandBotEnum;
import bot.entity.enums.MessageTemplateEnum;
import bot.service.business.MessageService;
import bot.service.business.StatsService;
import bot.service.business.UserService;
import bot.service.commands.AbstractProvider;
import com.pengrad.telegrambot.model.Message;
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
    private final StatsService statsService;
    private final UserService userService;
    private final MessageService messageService;

    private final static String ARCHI_PIDOR = "Главный архипидор только один {0}, а все остальные лишь его подсосы.";

    @Override
    public CommandBotEnum getCommand() {
        return CommandBotEnum.STATS;
    }

    /**
     * {@inheritDoc}
     *
     * @param message объект Message в рамках которого пришла команда на исполнение
     *                содержит в себе всю метаинформацию необходимую для выполнения команды
     */
    public void execute(@NotNull Message message) {
        final var chatId = message.chat().id();
        final var footer = messageService.randomMessage(MessageTemplateEnum.STATS);

        log.info("Запрос статистики в чате '{}'(idChat={})", message.chat().title(), message.chat().id());

        var statsList = statsService.findStats(chatId).stream()
            .sorted(Comparator.comparingLong(Stats::getCountRooster).reversed())
            .toList();

        var msg = new StringBuilder();
        if (ARCHI_PIDOR.equals(footer)) {
            var stats = statsList.get(0);
            var user = userService.findById(stats.getUserId());
            msg.append(MessageFormat.format(footer, user.toString()));
        } else {
            msg.append(footer).append("\n");
            for (int i = 0; i < statsList.size(); i++) {
                var stats = statsList.get(i);
                var user = userService.findById(stats.getUserId());
                msg.append(i + 1).append(") ").append(user.toString()).append(" ");
                msg.append(stats.getCountRooster()).append(" раз(а)\n");
            }
        }

        final var sendMessage = new SendMessage(chatId, msg.toString()).replyToMessageId(message.messageId());
        telegramBot.execute(sendMessage);
    }
}
