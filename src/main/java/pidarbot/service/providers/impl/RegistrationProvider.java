package pidarbot.service.providers.impl;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pidarbot.entity.domain.Stats;
import pidarbot.entity.domain.User;
import pidarbot.entity.enums.CommandBotEnum;
import pidarbot.service.business.StatsService;
import pidarbot.service.business.UserService;
import pidarbot.service.providers.CommandProvider;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationProvider implements CommandProvider {
    public static final String MSG_NEW_USER = "Ну шо, пидарасина, ты в игре";
    public static final String MSG_OLD_USER = "Ты че, дурак что ли? Ты уже зареган был, долбаёбина";
    private final UserService userService;
    private final StatsService statsService;
    private final TelegramBot telegramBot;

    @Override
    public CommandBotEnum getCommand() {
        return CommandBotEnum.REG;
    }

    synchronized public void execute(Message message) {
        boolean isNewUser = false;

        User user = userService.findByUserTelegramId(message.from().id());

        if (user == null) {
            user = saveNewUser(message.from());
            registrationUserOnGame(message.chat().id(), user.getId());
            isNewUser = true;
        }

        notifySuccessSaveUser(message, isNewUser);
    }

    synchronized private User saveNewUser(com.pengrad.telegrambot.model.User user) {
        User build = User.builder()
                .userTelegramId(user.id())
                .username(user.username())
                .firstName(user.firstName())
                .lastName(user.lastName())
                .build();
        log.info("Регистрация нового пользователя: {}", build);
        return userService.save(build);
    }

    synchronized private void registrationUserOnGame(Long chatId, Long userId) {
        Stats stats = new Stats(chatId, userId);
        statsService.save(stats);
    }

    synchronized private void notifySuccessSaveUser(Message message, boolean isNewUser) {
        String msg = isNewUser ? MSG_NEW_USER : MSG_OLD_USER;
        SendMessage sendMessage = new SendMessage(message.chat().id(), msg)
                .parseMode(ParseMode.HTML)
                .disableNotification(true)
                .replyToMessageId(message.messageId());
        SendResponse execute = telegramBot.execute(sendMessage);
        if (!execute.isOk()) {
            log.info(execute.description());
        }
    }
}
