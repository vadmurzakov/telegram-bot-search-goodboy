package bot.service.commands.providers;

import static bot.util.RandomUtils.generateRandomNumber;

import bot.entity.domain.Journal;
import bot.entity.domain.Lottery;
import bot.entity.domain.Stats;
import bot.entity.enums.CommandBotEnum;
import bot.entity.enums.MessageTemplateEnum;
import bot.service.business.JournalService;
import bot.service.business.LotteryService;
import bot.service.business.StatsService;
import bot.service.commands.AbstractProvider;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendDice;
import com.pengrad.telegrambot.request.SendMessage;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LotteryProvider extends AbstractProvider {
    private static final int LOTTERY_LAUNCH_PERIOD = 7;
    private static final String CALLBACK_RUN_LOTTERY = "callbackLottery(run)";
    private static final InlineKeyboardMarkup INLINE_KEYBOARD_MARKUP = new InlineKeyboardMarkup(
        new InlineKeyboardButton("1").callbackData("callbackLottery(1)"),
        new InlineKeyboardButton("2").callbackData("callbackLottery(2)"),
        new InlineKeyboardButton("3").callbackData("callbackLottery(3)"),
        new InlineKeyboardButton("5").callbackData("callbackLottery(5)")
    );

    private static final InlineKeyboardMarkup INLINE_KEYBOARD_MARKUP_WITH_RUN = new InlineKeyboardMarkup(
        new InlineKeyboardButton("1").callbackData("callbackLottery(1)"),
        new InlineKeyboardButton("2").callbackData("callbackLottery(2)"),
        new InlineKeyboardButton("3").callbackData("callbackLottery(3)"),
        new InlineKeyboardButton("5").callbackData("callbackLottery(5)")
    ).addRow(new InlineKeyboardButton("Запуск").callbackData("callbackLottery(run)"));

    private final LotteryService lotteryService;
    private final StatsService statsService;
    private final JournalService journalService;

    @Override
    public CommandBotEnum getCommand() {
        return CommandBotEnum.LOTTERY;
    }

    @Override
    public void execute(@NotNull Message message) {
        final var chatId = message.chat().id();
        final var chatTitle = message.chat().title();
        final var messageId = message.messageId();
        final var user = userService.findByUserTelegramId(message.from().id());

        // функционал лотереи выключен и будет удален позднее
        if ("enabled".equals("false") && checkAccess(message) && user.isPresent()) {
            log.info("[{}({})][{}] Запущена лотерея", chatTitle, chatId, user.get());
            var optionalLottery = lotteryService.findLottery(chatId, user.get().getId());

            final var msgLotteryStart = messageService.randomMessage(MessageTemplateEnum.LOTTERY_RUN);
            final var sendMessage = new SendMessage(chatId,
                MessageFormat.format(msgLotteryStart, safetyHtml(user.get().toString()), StringUtils.EMPTY))
                .disableNotification(true)
                .replyToMessageId(messageId)
                .parseMode(ParseMode.HTML)
                .replyMarkup(INLINE_KEYBOARD_MARKUP);
            final var startLotteryResponse = telegramBot.execute(sendMessage);
            log.info("[{}({})][{}] Номера выигрышных билетов: {}", chatTitle, chatId, user.get(), StringUtils.EMPTY);

            if (optionalLottery.isEmpty()) {
                var lottery = Lottery.builder()
                    .chatId(chatId)
                    .userId(user.get().getId())
                    .lastMessageId(startLotteryResponse.message().messageId())
                    .build();
                lotteryService.save(lottery);
            } else {
                var lottery = optionalLottery.get();
                lottery.setLastMessageId(startLotteryResponse.message().messageId());
                lotteryService.update(lottery);
            }
        }

    }

    /**
     * Удаляем старое сообщение с начатой лотереей, т.к. были сгенерены новые тикеты.
     *
     * @param message метаинформация о сообщении которое надо отредактировать.
     */
    private void deleteKeyboardFromOldLottery(Message message) {
        try {
            final var chatId = message.chat().id();
            final var messageId = message.messageId();
            final var text = message.text();

            var editMessage = new EditMessageText(chatId, messageId, text).replyMarkup(new InlineKeyboardMarkup());
            telegramBot.execute(editMessage);
        } catch (Exception e) {
            log.warn("не смог удалить клавиатуру у messageId={}: {}", message.messageId(), e.getMessage());
        }
    }

    @Override
    public void execute(@NotNull CallbackQuery callbackQuery) {
        final var chatId = callbackQuery.message().chat().id();
        final var chatTitle = callbackQuery.message().chat().title();
        final var messageId = callbackQuery.message().messageId();
        final var whoseCallback = callbackQuery.from().id();
        final var whoseRate = callbackQuery.message().replyToMessage().from().id();

        if (whoseRate.equals(whoseCallback) && !callbackQuery.data().equals(CALLBACK_RUN_LOTTERY)) {
            var user = userService.getByTelegramId(whoseCallback);
            var lottery = lotteryService.getLottery(chatId, user.getId());

            final var bidValue = callbackQuery.data().replaceAll("\\D", "");
            log.info("[{}({})][{}] Ставка принята на {}", chatTitle, chatId, user, bidValue);

            final var tickets = generateTickets(Integer.parseInt(bidValue));
            log.info("[{}({})][{}] Номера выигрышных билетов: {}", chatTitle, chatId, user, tickets);

            final var msgLotteryStart = messageService.randomMessage(MessageTemplateEnum.LOTTERY_RUN);
            var editMessage = new EditMessageText(chatId, messageId,
                MessageFormat.format(msgLotteryStart, safetyHtml(user.toString()), tickets))
                .replyMarkup(new InlineKeyboardMarkup())
                .parseMode(ParseMode.HTML)
                .replyMarkup(INLINE_KEYBOARD_MARKUP_WITH_RUN);
            telegramBot.execute(editMessage);

            lottery.setTickets(tickets);
            lottery.setBid(Integer.parseInt(bidValue));
            lotteryService.update(lottery);
        } else if (whoseRate.equals(whoseCallback) && callbackQuery.data().equals(CALLBACK_RUN_LOTTERY)) {
            var user = userService.getByTelegramId(whoseCallback);
            var lottery = lotteryService.getLottery(chatId, user.getId());

            deleteKeyboardFromOldLottery(callbackQuery.message());

            final var diceValue = sendDice(messageId, chatId);
            log.info("[{}({})][{}] Лотерейный билет №{}", chatTitle, chatId, user, diceValue);

            setLastDayRunLottery(lottery);

            var stat = statsService.getStat(chatId, user.getId());
            var resultLottery = updStatsAndGetResultLottery(lottery.getBid(), lottery.getTickets(), diceValue, stat);

            var request = new SendMessage(chatId, MessageFormat.format(resultLottery, user, lottery.getBid()))
                .disableNotification(true);
            telegramBot.execute(request);
            log.info("[{}({})][{}] {}", chatTitle, chatId, user, MessageFormat.format(resultLottery, user, lottery.getBid()));
        } else {
            final var answerCallbackQuery = new AnswerCallbackQuery(callbackQuery.id())
                .showAlert(true)
                .text("хуйню нажал, это не твоя ставка");
            telegramBot.execute(answerCallbackQuery);
        }
    }

    /**
     * Получение результата лотереи и обновление статистики.
     *
     * @param bidValue  размер ставки от пользователя.
     * @param tickets   множество билетов которые мы считаем выигрышными.
     * @param diceValue выпавший номер билетаж
     * @param stat      данные статистики игрока.
     * @return результат статистики который необходимо отправить игроку.
     */
    private String updStatsAndGetResultLottery(Integer bidValue, Set<Integer> tickets, Integer diceValue, Stats stat) {
        var countRooster = stat.getCountRooster();

        String resultLottery;
        if (tickets.contains(diceValue)) {
            //win
            long newCountRooster = countRooster - bidValue;
            stat.setCountRooster(newCountRooster < 0 ? 0 : newCountRooster);
            resultLottery = messageService.randomMessage(MessageTemplateEnum.LOTTERY_WIN);

            // убираем из журнала
            var journals = journalService.findAllByCurrentMonth(stat.getChatId())
                .stream()
                .filter(e -> e.getUserId().equals(stat.getUserId()))
                .toList()
                .listIterator();

            for (int i = 0; i < bidValue; i++) {
                if (journals.hasNext()) {
                    var journal = journals.next();
                    journalService.delete(journal);
                }
            }

        } else {
            // fail
            stat.setCountRooster(countRooster + bidValue);
            resultLottery = messageService.randomMessage(MessageTemplateEnum.LOTTERY_FAIL);
            // добавляем в журнал
            for (int i = 0; i < bidValue; i++) {
                journalService.save(new Journal(stat.getUserId(), stat.getChatId()));
            }
        }
        statsService.save(stat);
        return resultLottery;
    }

    /**
     * Установить дату последней игры в лотерею для игрока.
     *
     * @param lottery лотерейная сущность игрока.
     */
    private void setLastDayRunLottery(@NotNull Lottery lottery) {
        lottery.setLastDayRun(LocalDate.now());
        lotteryService.update(lottery);
    }

    /**
     * Кидаем кубик для определение номера билета игрока.
     *
     * @param messageId сообщение игрока для реплая.
     * @param chatId    куда отправлять.
     * @return номер билета игрока.
     */
    private Integer sendDice(@NotNull Integer messageId, Long chatId) {
        var sendDice = new SendDice(chatId);
        sendDice.replyToMessageId(messageId);
        sendDice.disableNotification(true);

        final var diceResponse = telegramBot.execute(sendDice);
        sleep(6000);
        return diceResponse.message().dice().value();
    }

    /**
     * Проверка что пользователь зарегистрирован в игре и лотерея запущена не чаще 1 раза в неделю.
     *
     * @param message метаданые "откуда", "от кого", "что".
     * @return true - если все ок и можно запускать лотерею.
     */
    @Override
    protected boolean checkAccess(Message message) {
        var accessCheck = super.checkAccess(message);

        // дополнительно проверим не играл ли он в лотерею последние LOTTERY_LAUNCH_PERIOD дней
        if (accessCheck) {
            final var user = userService.getByTelegramId(message.from().id());
            final var charId = message.chat().id();
            final var lottery = lotteryService.findLottery(charId, user.getId());

            if (lottery.isPresent() && lottery.get().getLastDayRun() != null) {
                final var lastDayRun = lottery.get().getLastDayRun();

                var period = Period.between(lastDayRun, LocalDate.now());
                var diff = Math.abs(period.getDays());

                if (diff < LOTTERY_LAUNCH_PERIOD) {
                    final var msg = messageService.randomMessage(MessageTemplateEnum.LOTTERY_ALREADY_RUN);
                    var request = new SendMessage(charId, MessageFormat.format(msg, lastDayRun))
                        .replyToMessageId(message.messageId())
                        .disableNotification(true);
                    telegramBot.execute(request);
                    return false;
                }
            }
        }
        return accessCheck;
    }

    /**
     * Генерирует рандомные числа из диапазона [1,6]
     * которые будут считать выигрышными билетами.
     * Количество выигрышных билетов рассчитывается на основе ставки.
     *
     * @param bid размер ставки
     * @return множество натуральных чисел из диапазона [1,6]
     */
    @NotNull
    private Set<Integer> generateTickets(int bid) {
        int countTickets = 0;
        switch (bid) {
            case 1 -> countTickets = 4;
            case 2 -> countTickets = 3;
            case 3 -> countTickets = 2;
            case 5 -> countTickets = 1;
        }
        var tickets = new HashSet<Integer>();
        while (tickets.size() < countTickets) {
            tickets.add(generateRandomNumber(6) + 1);
        }
        return tickets;
    }

    /**
     * Искуственные паузы перед отправками сообщения. Имитация долгого принятия решения.
     *
     * @param mills время в миллисекундах (1000мс = 1сек)
     */
    private static void sleep(int mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            log.error("Thread.sleep() упал с ошибкой: {}", e.getMessage());
        }
    }

    /**
     * Определение является ли данное сообщение в рамках контекста командой лотереи.
     * Определение команд становится сложнее из-за того, что приходится держать контект,
     * как например в лотерее идет взаимодействие с пользователем, а не тупая обработка команды.
     *
     * @param message сообщение из телеги со всей метаинфой.
     * @return true - если это сообщение часть лотереи.
     */
    @Override
    public boolean defineCommand(@NotNull Message message) {
        try {
            final var chatId = message.chat().id();
            final var userId = message.from().id();

            if (message.replyToMessage() != null) {
                final var replyMessageId = message.replyToMessage().messageId();
                final var user = userService.findByUserTelegramId(userId);
                if (user.isPresent()) {
                    final var lottery = lotteryService.findLottery(chatId, user.get().getId());
                    return lottery.isPresent() && lottery.get().getLastMessageId().equals(replyMessageId);
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
