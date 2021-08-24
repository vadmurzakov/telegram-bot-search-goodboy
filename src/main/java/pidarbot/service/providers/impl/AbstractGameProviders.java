package pidarbot.service.providers.impl;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import pidarbot.entity.domain.Stats;
import pidarbot.entity.domain.User;
import pidarbot.entity.enums.CommandBotEnum;
import pidarbot.service.business.StatsService;
import pidarbot.service.business.UserService;
import pidarbot.service.providers.CommandProviders;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static pidarbot.util.RandomUtil.generateRandomNumber;

@Slf4j
public abstract class AbstractGameProviders implements CommandProviders {

    @Autowired
    private StatsService statsService;
    @Autowired
    private UserService userService;
    @Autowired
    private TelegramBot telegramBot;

    protected abstract void incrementCount(Stats stats);
    protected abstract String getFormatMessageService();
    protected abstract LocalDate getLastDayRunGame(Stats stats);

    public void execute(Message message) {
        Long chatId = message.chat().id();
        List<Stats> statsList = statsService.findStats(chatId);

        if (isGameStartedToday(message, statsList)) return;;

        if(CollectionUtils.isEmpty(statsList)) return;

        statsList = filterStats(statsList);

        int randomNumber = generateRandomNumber(statsList.size());
        Stats stats = statsList.get(randomNumber - 1);
        incrementCount(stats);
        statsService.save(stats);

        User user = userService.findByUserId(stats.getUserId());

        String msg = getMessage(user);

        SendMessage sendMessage = new SendMessage(chatId, msg).replyToMessageId(message.messageId());
        telegramBot.execute(sendMessage);
    }

    /**
     * исключить из выборки тех, кто сегодня уже взял какую-то награду: пидора или красавчика
     * @param statsList - статистика всех кто участвует в игре
     */
    protected abstract List<Stats> filterStats(List<Stats> statsList);

    /**
     * Проверить запускался ли бот сегодня, чтобы не искать данные повторно
     * и отправить соответствующее уведомление в чат, если это необходимо
     *
     * @param statsList - список игроков в чате
     * @return false - если игра уже запускалась и данные найдены
     */
    protected boolean isGameStartedToday(Message message, List<Stats> statsList) {
        Optional<Stats> optionalStats = statsList.stream()
                .filter(e -> getLastDayRunGame(e) != null)
                .filter(e -> LocalDate.now().compareTo(getLastDayRunGame(e)) == 0)
                .findFirst();

        if (optionalStats.isPresent()) {
            Stats stats = optionalStats.get();
            User user = userService.findByUserId(stats.getUserId());

            String game = getCommand() == CommandBotEnum.PIDR ? "Пидор-Дня" : "Красавчик-Дня";

            String msg = "Запускали уже сегодня, " + game + ": " + user.getFullName();

            SendMessage sendMessage = new SendMessage(message.chat().id(), msg).replyToMessageId(message.messageId());
            telegramBot.execute(sendMessage);

            return true;
        }

        return false;
    }

    private String getMessage(User user) {
        String format = getFormatMessageService();
        return MessageFormat.format(format, user.getFullName());
    }

}
