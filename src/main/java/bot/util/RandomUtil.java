package bot.util;

/**
 * Utility-Класс для генерации псевдослучайных чисел
 */
public final class RandomUtil {
    /**
     * Возвращает рандомное число из диапазона [0, max)
     * @param max - верхняя планка рандомного числа
     * @return рандомное число [0, max]
     */
    public static int generateRandomNumber(int max) {
        return (int) (Math.random() * max);
    }
}