package bot.service.providers.impl;

import bot.entity.domain.Stats;
import bot.entity.domain.User;
import bot.entity.enums.CommandBotEnum;
import bot.service.business.StatsService;
import bot.service.business.UserService;
import bot.service.providers.CommandProvider;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationProvider implements CommandProvider {
    public static final String MSG_NEW_USER = "0J3RgyDRiNC+LCDQv9C40LTQsNGA0LDRgdC40L3QsCwg0YLRiyDQsiDQuNCz0YDQtQ==";
    public static final String MSG_OLD_USER = "0KLRiyDRh9C1LCDQtNGD0YDQsNC6INGH0YLQviDQu9C4PyDQotGLINGD0LbQtSDQt9Cw0YDQtdCz0LDQvSDQsdGL0LssINC00L7Qu9Cx0LDRkdCx0LjQvdCw";
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
        String msgEncode = isNewUser ? MSG_NEW_USER : MSG_OLD_USER;
        String msg = new String(Base64.getDecoder().decode(msgEncode));
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
