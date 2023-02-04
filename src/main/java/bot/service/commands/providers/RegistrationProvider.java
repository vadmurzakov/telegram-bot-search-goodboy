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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

/**
 * Реализация провайдера для комады /reg
 */
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
     * Регистрация нового игрока в игре.
     *
     * @param message объект Message в рамках которого пришла команда на исполнение
     *                содержит в себе всю метаинформацию необходимую для выполнения команды
     */
    @Override
    public void execute(final @NotNull Message message) {
        final var chatId = message.chat().id();
        final var messageId = message.messageId();
        final var telegramUser = message.from();

        final var user = saveUser(telegramUser);
        registrationGameForUser(chatId, messageId, user);
    }

    /**
     * Сохранение {@link User} в БД при необходимости,
     * является единой сущностью для игры во всех чатах.
     *
     * @param telegramUser данные пользователя из телеграмма.
     * @return {@link User} из БД.
     */
    @Nullable
    private User saveUser(com.pengrad.telegrambot.model.User telegramUser) {
        final User persistenceUser;
        var optionalUser = userService.findByUserTelegramId(telegramUser.id());
        if (optionalUser.isEmpty()) {
            var user = User.builder()
                .userTelegramId(telegramUser.id())
                .username(telegramUser.username())
                .firstName(telegramUser.firstName())
                .lastName(telegramUser.lastName())
                .build();
            persistenceUser = userService.save(user);
        } else {
            persistenceUser = optionalUser.get();
        }
        return persistenceUser;
    }

    /**
     * Регистрация игры для пользователя.
     *
     * @param chatId    чат в котором нужно разегистрировать пользователя как игрока.
     * @param messageId идентификатор сообщения от пользователя, необходимо чтобы сделать reply ответа.
     * @param user      {@link User} из БД.
     */
    private void registrationGameForUser(Long chatId, Integer messageId, User user) {
        var optionalStats = statsService.findStat(chatId, user.getId());
        if (optionalStats.isEmpty()) {
            statsService.save(new Stats(chatId, user.getId()));
            final var msg = messageService.randomMessage(MessageTemplateEnum.REGISTRATION);
            final var request = new SendSticker(chatId, msg).replyToMessageId(messageId);
            telegramBot.execute(request);
            log.info("В чате id={} новый игрок '{}'", chatId, user);
        } else {
            final var msg = messageService.randomMessage(MessageTemplateEnum.USER_ALREADY_PLAYING);
            final var request = new SendMessage(chatId, msg).replyToMessageId(messageId);
            telegramBot.execute(request);
            log.info("Пользователь '{}' уже играет в чате с id={}", user, chatId);
        }
    }

}
