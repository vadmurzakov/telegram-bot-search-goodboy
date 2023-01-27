package bot.service.providers.impl;

import bot.entity.domain.Client;
import bot.entity.domain.Stats;
import bot.entity.enums.CommandBotEnum;
import bot.service.business.MessageService;
import bot.service.business.StatsService;
import bot.service.business.UserService;
import bot.service.providers.CommandProvider;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import java.text.MessageFormat;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class StatsProvider implements CommandProvider {
    private final StatsService statsService;
    private final UserService userService;
    private final TelegramBot telegramBot;
    private final MessageService messageService;

    private final static String ARCHI_PIDOR = "Главный архипидор только один {0}, а все остальные лишь его подсосы.";

    @Override
    public CommandBotEnum getCommand() {
        return CommandBotEnum.STATS;
    }

    public void execute(Message message) {
        Long chatId = message.chat().id();
        log.info("Запрос статистики в чате {}(idChat={})", message.chat().title(), message.chat().id());
        List<Stats> statsList = statsService.findStats(chatId);
        String footer = messageService.randomStatsMessage();

        statsList = statsList.stream().sorted(Comparator.comparingLong(Stats::getCountRooster).reversed()).collect(Collectors.toList());

        StringBuilder msg = new StringBuilder();
        if (ARCHI_PIDOR.equals(footer)) {
            Stats stats = statsList.get(0);
            Client user = userService.findByUserId(stats.getUserId());
            msg.append(MessageFormat.format(footer, user.toString()));
        } else {
            msg.append(footer);
            msg.append("\n");
            for (int i = 0; i < statsList.size(); i++) {
                Stats stats = statsList.get(i);
                Client user = userService.findByUserId(stats.getUserId());
                msg.append(i + 1).append(") ").append(user.toString()).append(" ");
                msg.append(stats.getCountRooster()).append(" раз(а)\n");
            }
        }

        SendMessage sendMessage = new SendMessage(chatId, msg.toString()).replyToMessageId(message.messageId());
        telegramBot.execute(sendMessage);
    }
}
