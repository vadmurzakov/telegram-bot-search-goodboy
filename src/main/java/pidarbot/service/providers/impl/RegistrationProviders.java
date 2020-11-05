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
import pidarbot.entity.enums.CommandBotEnum;
import pidarbot.service.business.StatsService;
import pidarbot.service.business.UserService;
import pidarbot.service.providers.CommandProviders;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationProviders implements CommandProviders {
    private final UserService userService;
    private final StatsService statsService;
    private final TelegramBot telegramBot;

    @Override
    public CommandBotEnum getCommand() {
        return CommandBotEnum.REG;
    }


    synchronized public void execute(Message message) {
        boolean isNewUser = false;
        Integer userId = message.from().id();
        pidarbot.entity.domain.User user = userService.findByUserTelegramId(userId);
        if (user == null) {
            user = userService.findByUsername(message.from().username());
        }

        if (user == null) {
            //todo[vmurzakov]: заменить на MapStruct
            user = pidarbot.entity.domain.User.builder()
                    .userTelegramId(userId)
                    .username(message.from().username())
                    .firstName(message.from().firstName())
                    .lastName(message.from().lastName())
                    .build();
            log.info("Сохранение нового пидарасины: {}", user);
            userService.save(user);
            isNewUser = true;
        }

        Long chatId = message.chat().id();
        Stats stat = statsService.findStat(chatId, user.getId());
        if (stat == null) {
            registrationUserOnGame(chatId, user.getId());
            isNewUser = true;
        }

        notifySuccessSaveUser(message, isNewUser);
    }

    synchronized private void registrationUserOnGame(Long chatId, Long userId) {
        Stats stats = new Stats(chatId, userId);
        statsService.save(stats);
    }

    synchronized private void notifySuccessSaveUser(Message message, boolean isNewUser) {
        String msg = isNewUser ? "Ну шо, пидарасина, ты в игре" : "Ты че, дурак что ли? Ты уже зареган был, долбаёбина";
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
