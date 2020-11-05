package pidarbot.service.providers.impl;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import liquibase.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pidarbot.entity.domain.Stats;
import pidarbot.entity.domain.User;
import pidarbot.entity.enums.CommandBotEnum;
import pidarbot.service.business.StatsService;
import pidarbot.service.business.UserService;
import pidarbot.service.providers.CommandProviders;

import java.util.List;

/**
 * @author Murzakov Vadim <murzakov.vadim@otr.ru>
 */
@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class GoodBoyProviders implements CommandProviders {
    private final StatsService statsService;
    private final UserService userService;
    private final TelegramBot telegramBot;

    @Override
    public CommandBotEnum getCommand() {
        return CommandBotEnum.PIDR;
    }

    public void execute(Message message) {
        Long chatId = message.chat().id();
        List<Stats> statsList = statsService.findStats(chatId);
        log.info("В игре Красавчик-Дня из чата {}, учавствуют {} игроков", chatId, statsList.size());

        if(statsList.size() == 0) return;

        int randomNumber = generateRandomNumber(statsList.size());
        Stats stats = statsList.get(randomNumber - 1);
        stats.setCountGoodBoy(stats.getCountGoodBoy() + 1);

        User user = userService.findByUserId(stats.getUserId());

        String msg = "Сегодня Красаучик-Дня: " + user.getFullName() + " (";
        if (StringUtils.isNotEmpty(user.getUsername())) msg += "@" + user.getUsername() + ")";
        SendMessage sendMessage = new SendMessage(chatId, msg);
        telegramBot.execute(sendMessage);
    }

    private int generateRandomNumber(int max) {
        return 1 + (int) (Math.random() * max);
    }
}
