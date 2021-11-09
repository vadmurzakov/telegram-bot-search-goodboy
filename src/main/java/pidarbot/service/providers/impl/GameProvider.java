package pidarbot.service.providers.impl;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pidarbot.entity.domain.Stats;
import pidarbot.entity.domain.User;
import pidarbot.entity.enums.CommandBotEnum;
import pidarbot.service.business.MessageService;
import pidarbot.service.business.StatsService;
import pidarbot.service.business.UserService;
import pidarbot.service.providers.CommandProvider;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static pidarbot.util.RandomUtil.generateRandomNumber;

@Slf4j
@Component
@AllArgsConstructor
public class GameProvider implements CommandProvider {

    private StatsService statsService;
    private UserService userService;
    private TelegramBot telegramBot;
    private MessageService messageService;

    @Override
    public CommandBotEnum getCommand() {
        return CommandBotEnum.GAME;
    }

    public void execute(Message message) {
        Long chatId = message.chat().id();
        List<Stats> statsList = statsService.findStats(chatId);

        if (isGameStartedToday(message, statsList)) {
            log.warn("Для чата {}({}) игра уже запускалась", message.chat().title(), message.chat().id());
            return;
        }

        if (CollectionUtils.isEmpty(statsList)) {
            log.warn("В чате {} не зарегистрировано ни одного игрока", chatId);
            return;
        }

        if (statsList.size() == 1) {
            log.error("Для запуска игры, в чате {} должно быть более 1 зарегистрированного игрока", chatId);
            return;
        }

        int randomPidr = 1;
        int randomGoodBoy = 1;
        String msg;

        while (randomPidr == randomGoodBoy) {
            randomPidr = generateRandomNumber(statsList.size());
            randomGoodBoy = generateRandomNumber(statsList.size());
        }

        Stats stats = statsList.get(randomPidr);
        stats.setCountPidrDay(stats.getCountPidrDay() + 1);
        stats.setLastDayPidr(LocalDate.now());
        statsService.save(stats);

        User user = userService.findByUserId(stats.getUserId());
        msg = MessageFormat.format(messageService.randomPidrMessage(), user.getFullName());

        stats = statsList.get(randomGoodBoy);
        stats.setCountGoodBoy(stats.getCountGoodBoy() + 1);
        stats.setLastDayGoodBoy(LocalDate.now());
        statsService.save(stats);
        user = userService.findByUserId(stats.getUserId());
        msg += "\n" + MessageFormat.format(messageService.randomGoodBoyMessage(), user.getFullName());

        SendMessage sendMessage = new SendMessage(chatId, msg).replyToMessageId(message.messageId());
        telegramBot.execute(sendMessage);
    }

    /**
     * Проверить запускался ли бот сегодня, чтобы не искать данные повторно
     * и отправить соответствующее уведомление в чат, если это необходимо
     *
     * @param statsList - список игроков в чате
     * @return false - если игра уже запускалась и данные найдены
     */
    protected boolean isGameStartedToday(Message message, List<Stats> statsList) {
        Optional<Stats> optionalStatsPidr = statsList.stream()
                .filter(e -> e.getLastDayPidr() != null)
                .filter(e -> LocalDate.now().compareTo(e.getLastDayPidr()) == 0)
                .findFirst();

        Optional<Stats> optionalStatsGoodBoy = statsList.stream()
                .filter(e -> e.getLastDayGoodBoy() != null)
                .filter(e -> LocalDate.now().compareTo(e.getLastDayGoodBoy()) == 0)
                .findFirst();

        if (optionalStatsPidr.isPresent() && optionalStatsGoodBoy.isPresent()) {
            User pidrToday = userService.findByUserId(optionalStatsPidr.get().getUserId());
            User goodBoyToday = userService.findByUserId(optionalStatsGoodBoy.get().getUserId());

            String msg = "Запускали уже сегодня\n\n" +
                    "Пидр дня: " + pidrToday.getFullName() + "\n" +
                    "Красаучик дня: " + goodBoyToday.getFullName();

            SendMessage sendMessage = new SendMessage(message.chat().id(), msg).replyToMessageId(message.messageId());
            telegramBot.execute(sendMessage);

            return true;
        }

        return false;
    }

}
