package bot.service.providers.impl;

import static bot.util.RandomUtil.generateRandomNumber;

import bot.entity.domain.Journal;
import bot.entity.domain.Stats;
import bot.entity.domain.User;
import bot.entity.enums.CommandBotEnum;
import bot.service.business.JournalService;
import bot.service.business.MessageService;
import bot.service.business.StatsService;
import bot.service.business.UserService;
import bot.service.providers.CommandProvider;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Slf4j
@Component
@AllArgsConstructor
public class GameProvider implements CommandProvider {

    private StatsService statsService;
    private UserService userService;
    private TelegramBot telegramBot;
    private MessageService messageService;
    private JournalService journalService;

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

        int randomRooster = generateRandomNumber(statsList.size());
        String msg;

        Stats stats = statsList.get(randomRooster);
        User user = userService.findByUserId(stats.getUserId());
        msg = MessageFormat.format(messageService.randomRoosterMessage(), user.getFullName());

        log.info("Выбор пал на {}", user.getFullName());

        SendMessage sendMessage = new SendMessage(chatId, msg).replyToMessageId(message.messageId());
        SendResponse execute = telegramBot.execute(sendMessage);

        stats.setCountRooster(stats.getCountRooster() + 1);
        stats.setLastDayRooster(LocalDate.now());
        stats.setLastMessageId(execute.message().messageId());
        statsService.save(stats);
        journalService.save(new Journal(user.getUserTelegramId()));
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

        if (optionalStatsRooster.isPresent()) {
            String msg = messageService.randomAlreadyStartedMessage();

            Integer replyToMessageId = optionalStatsRooster.get().getLastMessageId();

            SendMessage sendMessage = new SendMessage(message.chat().id(), msg).replyToMessageId(replyToMessageId);
            telegramBot.execute(sendMessage);

            return true;
        }

        return false;
    }

}
