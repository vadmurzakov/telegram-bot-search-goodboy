package bot.service.commands.providers;

import static bot.util.RandomUtils.generateRandomNumber;

import bot.entity.domain.Journal;
import bot.entity.domain.Lottery;
import bot.entity.domain.Stats;
import bot.entity.domain.User;
import bot.entity.enums.CommandBotEnum;
import bot.entity.enums.MessageTemplateEnum;
import bot.service.business.JournalService;
import bot.service.business.LotteryService;
import bot.service.business.StatsService;
import bot.service.commands.AbstractProvider;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendChatAction;
import com.pengrad.telegrambot.request.SendDice;
import com.pengrad.telegrambot.request.SendMessage;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LotteryProvider extends AbstractProvider {
    private static final String EMOJI = "\uD83C\uDFB2";
    private static final int LOTTERY_LAUNCH_PERIOD = 7;
    private static final Set<Integer> CORRECT_BID = Set.of(1, 2, 3);

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
        final var messageReply = Optional.ofNullable(message.replyToMessage());
        final var user = userService.findByUserTelegramId(message.from().id());

        if (accessCheck(message) && user.isPresent()) {

            var optionalLottery = lotteryService.findLottery(chatId, user.get().getId());
            var lastLotteryMessage = optionalLottery.map(Lottery::getLastMessageId).orElse(null);

            if (messageReply.isPresent() && messageReply.get().messageThreadId().equals(lastLotteryMessage)) {
                final var bidValue = message.text();

                if (correctBid(bidValue)) {
                    log.info("[{}({})][{}] Ставка на {}", chatTitle, chatId, user.get(), bidValue);

                    var tickets = generateTickets();
                    log.info("[{}({})][{}] Номера выигрышных билетов: {}", chatTitle, chatId, user.get(), tickets);

                    sendTickets(chatId, user, tickets);
                    final var diceValue = sendDice(messageId, chatId);
                    log.info("[{}({})][{}] Лотерейный билет №{}", chatTitle, chatId, user.get(), diceValue);

                    setLastDayRunLottery(optionalLottery);

                    var stat = statsService.findStat(chatId, user.get().getId()).get();
                    String resultLottery = updateStatsAndGetResultLottery(bidValue, tickets, diceValue, stat);

                    var request = new SendMessage(chatId, MessageFormat.format(resultLottery, user.get(), bidValue))
                        .disableNotification(true);
                    telegramBot.execute(request);
                } else {
                    log.info("[{}({})][{}] Некорректная ставка '{}'", chatTitle, chatId, user.get(), bidValue);
                    final var msg = messageService.randomMessage(MessageTemplateEnum.LOTTERY_INCORRECT_BID);
                    telegramBot.execute(new SendMessage(chatId, msg).replyToMessageId(messageId));
                }
            } else {
                log.info("[{}({})][{}] Запущена лотерея", chatTitle, chatId, user.get());
                final var lotteryStart = messageService.randomMessage(MessageTemplateEnum.LOTTERY_RUN);
                final var sendMessage = new SendMessage(chatId, MessageFormat.format(lotteryStart, user.get()))
                    .replyToMessageId(messageId)
                    .disableNotification(true);
                final var startLottery = telegramBot.execute(sendMessage);

                final var messageThreadId = startLottery.message().messageThreadId();

                if (optionalLottery.isEmpty()) {
                    var lottery = new Lottery(chatId, user.get().getId(), messageThreadId);
                    lotteryService.save(lottery);
                } else {
                    var lottery = optionalLottery.get();
                    lottery.setLastMessageId(messageThreadId);
                    lotteryService.update(lottery);
                }
            }
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
    private String updateStatsAndGetResultLottery(String bidValue, Set<Integer> tickets, Integer diceValue,
                                                  Stats stat) {
        var countRooster = stat.getCountRooster();

        String resultLottery;
        if (tickets.contains(diceValue)) {
            //win
            long newCountRooster = countRooster - Long.parseLong(bidValue);
            stat.setCountRooster(newCountRooster < 0 ? 0 : newCountRooster);
            resultLottery = messageService.randomMessage(MessageTemplateEnum.LOTTERY_WIN);

            // убираем из журнала
            var journals = journalService.findAllByCurrentMonth(stat.getChatId())
                .stream()
                .filter(e -> e.getUserId().equals(stat.getUserId()))
                .toList()
                .listIterator();

            for (int i = 0; i < Long.parseLong(bidValue); i++) {
                if (journals.hasNext()) {
                    var journal = journals.next();
                    journalService.delete(journal);
                }
            }

        } else {
            // fail
            stat.setCountRooster(countRooster + Long.parseLong(bidValue));
            resultLottery = messageService.randomMessage(MessageTemplateEnum.LOTTERY_FAIL);
            // добавляем в журнал
            for (int i = 0; i < Long.parseLong(bidValue); i++) {
                journalService.save(new Journal(stat.getUserId(), stat.getChatId()));
            }
        }
        statsService.save(stat);
        return resultLottery;
    }

    /**
     * Установить дату последней игры в лотерею для игрока.
     *
     * @param optionalLottery лотерейная сущность игрока.
     */
    private void setLastDayRunLottery(@NotNull Optional<Lottery> optionalLottery) {
        var lottery = optionalLottery.get();
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
        sendDice.emoji(EMOJI);
        sendDice.replyToMessageId(messageId);
        sendDice.disableNotification(true);

        final var diceResponse = telegramBot.execute(sendDice);
        sleep(6000);
        return diceResponse.message().dice().value();
    }

    /**
     * Отправка номера билетов которые будут выигрышными.
     *
     * @param chatId  куда отправлять
     * @param user    для кого
     * @param tickets номера выигрышных билетов.
     */
    private void sendTickets(Long chatId, @NotNull Optional<User> user, Set<Integer> tickets) {
        final var msgTicket = messageService.randomMessage(MessageTemplateEnum.LOTTERY_TICKET);
        final var request = new SendMessage(chatId, MessageFormat.format(msgTicket, user.get(), tickets.toString()))
            .disableNotification(true);
        telegramBot.execute(request);
        telegramBot.execute(new SendChatAction(chatId, "typing"));
        sleep(4000);
    }

    /**
     * Проверка корректности ставки. Ставка должна быть от 1 до 2 очков.
     *
     * @param bidValue ставка пользователя.
     * @return true - если все ок.
     */
    private boolean correctBid(String bidValue) {
        try {
            return CORRECT_BID.contains(Integer.parseInt(bidValue));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Проверка что пользователь зарегистрирован в игре и лотерея запущена не чаще 1 раза в неделю.
     *
     * @param message метаданые "откуда", "от кого", "что".
     * @return true - если все ок и можно запускать лотерею.
     */
    @Override
    protected boolean accessCheck(Message message) {
        var accessCheck = super.accessCheck(message);
        // если доступ к лотерее разрешен, дополнительно проверим не играл ли он в неё последнюю неделю
        if (accessCheck) {
            final var user = userService.findByUserTelegramId(message.from().id()).get();
            final var charId = message.chat().id();
            final var lottery = lotteryService.findLottery(charId, user.getId());
            if (lottery.isPresent() && lottery.get().getLastDayRun() != null) {
                final var lastDayRun = lottery.get().getLastDayRun();
                final var now = LocalDate.now();

                var period = Period.between(lastDayRun, now);
                var diff = Math.abs(period.getDays());

                if (diff < LOTTERY_LAUNCH_PERIOD) {
                    final var msg = messageService.randomMessage(MessageTemplateEnum.LOTTERY_ALREADY_RUN);
                    telegramBot.execute(new SendMessage(charId, MessageFormat.format(msg, lastDayRun)).replyToMessageId(
                        message.messageId()));
                    return false;
                }
            }
        }
        return accessCheck;
    }

    /**
     * Генерирует рандомные 3 числа из диапазона [1,6]
     * которые будут считать выигрышными билетами.
     *
     * @return множество натуральных чисел из диапазона [1,6]
     */
    @NotNull
    private Set<Integer> generateTickets() {
        var tickets = new HashSet<Integer>();
        while (tickets.size() < 3) {
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
                final var replyMessageId = message.replyToMessage().messageThreadId();
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
