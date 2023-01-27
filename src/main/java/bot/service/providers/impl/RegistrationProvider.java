package bot.service.providers.impl;

import bot.entity.domain.Client;
import bot.entity.domain.Stats;
import bot.entity.enums.CommandBotEnum;
import bot.service.business.StatsService;
import bot.service.business.UserService;
import bot.service.providers.CommandProvider;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendSticker;
import com.pengrad.telegrambot.response.SendResponse;
import java.util.Base64;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationProvider implements CommandProvider {
    public static final String MSG_NEW_USER = "CAACAgIAAxkBAAEHcyNj0jQ0oF0zeAyjL-bpil_2t8yckgACDxEAAojF0Usu_WLHr8Vjzi0E";
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

        //todo[vmurzakov]: тут надо искать не только по ид-пользователя, но и в разрезе ид-чата
        Client user = userService.findByUserTelegramId(Long.valueOf(message.from().id()));

        if (user == null) {
            user = saveNewUser(message.from());
            registrationUserOnGame(message.chat().id(), user.getId());
            isNewUser = true;
        } else {
            log.info("Пользователь {} уже зарегестрирован в чате {}", user.toString(), message.chat().id());
        }

        notifySuccessSaveUser(message, isNewUser);
    }

    synchronized private Client saveNewUser(com.pengrad.telegrambot.model.User user) {
        Client build = Client.builder()
                .userTelegramId(Long.valueOf(user.id()))
                .username(user.username())
                .firstName(user.firstName())
                .lastName(user.lastName())
                .build();
        log.info("Регистрация нового пользователя: {}", build);
        return userService.save(build);
    }

    synchronized private void registrationUserOnGame(Long chatId, UUID userId) {
        Stats stats = new Stats(chatId, userId);
        statsService.save(stats);
    }

    synchronized private void notifySuccessSaveUser(Message message, boolean isNewUser) {
        if (isNewUser) {
            SendSticker sendSticker = new SendSticker(message.chat().id(), MSG_NEW_USER)
                .replyToMessageId(message.messageId());
            SendResponse execute = telegramBot.execute(sendSticker);
        } else {
            String msg = new String(Base64.getDecoder().decode(MSG_OLD_USER));
            SendMessage sendMessage = new SendMessage(message.chat().id(), msg)
                .parseMode(ParseMode.HTML)
                .disableNotification(true)
                .replyToMessageId(message.messageId());
            SendResponse execute = telegramBot.execute(sendMessage);
        }
    }
}
