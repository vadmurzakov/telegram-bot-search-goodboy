package bot.service.providers.impl;

import bot.entity.domain.Stats;
import bot.entity.domain.User;
import bot.entity.enums.CommandBotEnum;
import bot.service.business.MessageService;
import bot.service.business.StatsService;
import bot.service.business.UserService;
import bot.service.providers.CommandProvider;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static bot.util.RandomUtil.generateRandomNumber;

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

        int randomRooster = 1;
        int randomGoodBoy = 1;
        String msg;

        while (randomRooster == randomGoodBoy) {
            randomRooster = generateRandomNumber(statsList.size());
            randomGoodBoy = generateRandomNumber(statsList.size());
        }

        Stats stats = statsList.get(randomRooster);
        stats.setCountRooster(stats.getCountRooster() + 1);
        stats.setLastDayRooster(LocalDate.now());
        statsService.save(stats);

        User user = userService.findByUserId(stats.getUserId());
        msg = MessageFormat.format(messageService.randomRoosterMessage(), user.getFullName());

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
        Optional<Stats> optionalStatsRooster = statsList.stream()
                .filter(e -> e.getLastDayRooster() != null)
                .filter(e -> LocalDate.now().compareTo(e.getLastDayRooster()) == 0)
                .findFirst();

        Optional<Stats> optionalStatsGoodBoy = statsList.stream()
                .filter(e -> e.getLastDayGoodBoy() != null)
                .filter(e -> LocalDate.now().compareTo(e.getLastDayGoodBoy()) == 0)
                .findFirst();

        if (optionalStatsRooster.isPresent() && optionalStatsGoodBoy.isPresent()) {
            User roosterToday = userService.findByUserId(optionalStatsRooster.get().getUserId());
            User goodBoyToday = userService.findByUserId(optionalStatsGoodBoy.get().getUserId());

            String msg = "Запускали уже сегодня\n\n" +
                    "Пушок дня: " + roosterToday.getFullName() + "\n" +
                    "Красаучик дня: " + goodBoyToday.getFullName();

            SendMessage sendMessage = new SendMessage(message.chat().id(), msg).replyToMessageId(message.messageId());
            telegramBot.execute(sendMessage);

            return true;
        }

        return false;
    }

}
