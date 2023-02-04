package bot.service.commands.providers;

import bot.entity.domain.Stats;
import bot.entity.domain.User;
import bot.entity.enums.CommandBotEnum;
import bot.entity.enums.MessageTemplateEnum;
import bot.service.business.MessageService;
import bot.service.business.StatsService;
import bot.service.business.UserService;
import bot.service.commands.AbstractProvider;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendSticker;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationProvider extends AbstractProvider {
    private final UserService userService;
    private final StatsService statsService;
    private final MessageService messageService;

    @Override
    public CommandBotEnum getCommand() {
        return CommandBotEnum.REG;
    }

    /**
     * {@inheritDoc}.
     *
     * @param message объект Message в рамках которого пришла команда на исполнение
     *                содержит в себе всю метаинформацию необходимую для выполнения команды
     */
    @Override
    public void execute(final @NotNull Message message) {
        final var chatId = message.chat().id();
        final var messageId = message.messageId();
        final var telegramUser = message.from();

        final UUID userId = registrationUser(telegramUser);
        registrationGameForUser(chatId, messageId, userId);
    }

    /**
     * Регистрация пользователя.
     * Если пользователь новый, то создается новый {@link User}, иначе возвращается id-существующего.
     *
     * @param telegramUser данные пользователя из телеграмма.
     * @return идентификатор {@link User} из БД.
     */
    @Nullable
    private UUID registrationUser(com.pengrad.telegrambot.model.User telegramUser) {
        final UUID userId;
        var optionalUser = userService.findByUserTelegramId(telegramUser.id());
        if (optionalUser.isEmpty()) {
            var user = User.builder()
                .userTelegramId(telegramUser.id())
                .username(telegramUser.username())
                .firstName(telegramUser.firstName())
                .lastName(telegramUser.lastName())
                .build();
            var persistenceUser = userService.save(user);
            userId = persistenceUser.getId();
        } else {
            userId = optionalUser.get().getId();
        }
        return userId;
    }

    /**
     * Регистрация игры для пользователя.
     *
     * @param chatId    чат в котором нужно разегистрировать пользователя как игрока.
     * @param messageId идентификатор сообщения от пользователя, необходимо чтобы сделать reply ответа.
     * @param userId    идентификатор существующего пользователя в БД.
     */
    private void registrationGameForUser(Long chatId, Integer messageId, UUID userId) {
        var optionalStats = statsService.findStat(chatId, userId);
        if (optionalStats.isEmpty()) {
            statsService.save(new Stats(chatId, userId));
            final var msg = messageService.randomMessage(MessageTemplateEnum.REGISTRATION);
            final var request = new SendSticker(chatId, msg).replyToMessageId(messageId);
            telegramBot.execute(request);
            log.info("В чате id={} новый игрок id={}", chatId, userId);
        } else {
            final var msg = messageService.randomMessage(MessageTemplateEnum.USER_ALREADY_PLAYING);
            final var request = new SendMessage(chatId, msg).replyToMessageId(messageId);
            telegramBot.execute(request);
            log.info("Пользователь с id={} уже играет в чате с id={}", userId, chatId);
        }
    }

}
