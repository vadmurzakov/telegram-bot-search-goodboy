package pidarbot.service.providers.impl;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pidarbot.entity.domain.Stats;
import pidarbot.entity.domain.User;
import pidarbot.entity.enums.CommandBotEnum;
import pidarbot.service.business.StatsService;
import pidarbot.service.business.UserService;
import pidarbot.service.providers.CommandProvider;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class StatsProvider implements CommandProvider {
    private final StatsService statsService;
    private final UserService userService;
    private final TelegramBot telegramBot;

    @Override
    public CommandBotEnum getCommand() {
        return CommandBotEnum.STATS;
    }

    public void execute(Message message) {
        Long chatId = message.chat().id();
        log.info("Запрос статистики в чате {}({})", message.chat().title(), message.chat().id());
        List<Stats> statsList = statsService.findStats(chatId);

        statsList = statsList.stream().sorted(Comparator.comparingLong(Stats::getCountPidrDay).reversed()).collect(Collectors.toList());

        StringBuilder msg = new StringBuilder();
        msg.append("Результаты Пидор-Дня\n");
        for (int i = 0; i < statsList.size(); i++) {
            Stats stats = statsList.get(i);
            User user = userService.findByUserId(stats.getUserId());
            msg.append(i + 1).append(") ").append(user.getFullName()).append(" ");
            msg.append(stats.getCountPidrDay()).append(" раз(а)\n");
        }

        statsList = statsList.stream().sorted(Comparator.comparingLong(Stats::getCountGoodBoy).reversed()).collect(Collectors.toList());

        msg.append("\nРезультаты Красаучик-Дня\n");
        for (int i = 0; i < statsList.size(); i++) {
            Stats stats = statsList.get(i);
            User user = userService.findByUserId(stats.getUserId());
            msg.append(i + 1).append(") ").append(user.getFullName()).append(" ");
            msg.append(stats.getCountGoodBoy()).append(" раз(а)\n");
        }

        SendMessage sendMessage = new SendMessage(chatId, msg.toString()).replyToMessageId(message.messageId());
        telegramBot.execute(sendMessage);
    }
}
