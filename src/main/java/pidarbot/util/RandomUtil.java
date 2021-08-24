package pidarbot.util;

/**
 * Utility-Класс для генерации псевдослучайных чисел
 */
public final class RandomUtil {
    /**
     * Возвращает рандомное число из диапазона [1, max]
     * @param max - верхняя планка рандомного числа
     * @return рандомное число [1, max]
     */
    public static int generateRandomNumber(int max) {
        return 1 + (int) (Math.random() * max);
    }
}