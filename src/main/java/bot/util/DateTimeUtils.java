package bot.util;

/**
 * Utility - класс для работы с датами и временем
 */
public final class DateTimeUtils {
    public static final String[] NAME_MONTHS = {
        "январь", "февраль", "март", "апрель", "май", "июнь",
        "июль", "август", "сентябрь", "октябрь", "ноябрь", "декабрь"};

    /**
     * Получить название месяца по его номеру.
     *
     * @param number номер месяцаю
     * @return название месяца.
     */
    public static String getNameMonth(int number) {
        return NAME_MONTHS[number];
    }

}
