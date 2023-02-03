package bot.service.providers.impl;

import static bot.util.RandomUtil.generateRandomNumber;

import bot.entity.domain.Journal;
import bot.entity.domain.Stats;
import bot.entity.enums.CommandBotEnum;
import bot.entity.enums.MessageTemplateEnum;
import bot.service.business.JournalService;
import bot.service.business.MessageService;
import bot.service.business.StatsService;
import bot.service.business.UserService;
import bot.service.providers.CommandProvider;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
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

    /**
     * {@inheritDoc}
     *
     * @param message объект Message в рамках которого пришла команда на исполнение
     *                содержит в себе всю метаинформацию необходимую для выполнения команды
     */
    @Override
    public void execute(Message message) {
        final var chatId = message.chat().id();
        final var messageId = message.messageId();
        final var chatTitle = message.chat().title();

        var statsList = statsService.findStats(chatId);
        if (CollectionUtils.isEmpty(statsList)) {
            log.info("В чате '{}'({}) не зарегистрировано ни одного игрока", chatTitle, chatId);
            final var msg = messageService.randomMessage(MessageTemplateEnum.NO_ACTIVE_PLAYERS);
            final var request = new SendMessage(chatId, msg).replyToMessageId(messageId);
            telegramBot.execute(request);
            return;
        }

        final var gameStartedToday = isGameStartedToday(statsList);
        if (gameStartedToday.isPresent()) {
            log.info("Для чата '{}'({}) игра уже запускалась", chatTitle, chatId);
            final var user = userService.findById(gameStartedToday.get().getUserId());
            final var msg = MessageFormat.format(messageService.randomMessage(MessageTemplateEnum.ALREADY_STARTED), user.toString());
            final var request = new SendMessage(chatId, msg).replyToMessageId(messageId);
            telegramBot.execute(request);
        } else {
            int randomRooster = generateRandomNumber(statsList.size());

            var stats = statsList.get(randomRooster);
            final var user = userService.findById(stats.getUserId());
            final String msg;
            if (user.getUserTelegramId().equals(message.from().id())) {
                msg = MessageFormat.format(messageService.randomMessage(MessageTemplateEnum.ROOSTER_WHO_CALLED), user.toString());
            } else {
                msg = MessageFormat.format(messageService.randomMessage(MessageTemplateEnum.ROOSTER), user.toString());
            }

            final var request = new SendMessage(chatId, msg).replyToMessageId(messageId);
            final var execute = telegramBot.execute(request);

            stats.setCountRooster(stats.getCountRooster() + 1);
            stats.setLastDayRooster(LocalDate.now());
            stats.setLastMessageId(execute.message().messageId());
            statsService.save(stats);
            journalService.save(new Journal(user.getId(), chatId));
            log.info("Запущена игра в чате '{}'({}): '{}'", chatTitle, chatId, msg);
        }
    }

    /**
     * Проверить запускалась ли игра сегодня.
     *
     * @param statsList - список игроков в чате
     * @return true - если игра сегодня запускалась, false - если игра сегодня не запускалась
     */
    protected Optional<Stats> isGameStartedToday(List<Stats> statsList) {
        return statsList.stream()
            .filter(e -> e.getLastDayRooster() != null)
            .filter(e -> LocalDate.now().isEqual(e.getLastDayRooster()))
            .findFirst();
    }

}
