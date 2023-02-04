package bot.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    @Test
    void declensionOfNumbers_0() {
        final var countValue = 0;
        assertEquals(getNameCount(countValue), "раз");
    }

    @Test
    void declensionOfNumbers_1() {
        final var countValue = 1;
        assertEquals(getNameCount(countValue), "раз");
    }

    @Test
    void declensionOfNumbers_2() {
        final var countValue = 2;
        assertEquals(getNameCount(countValue), "раза");
    }

    @Test
    void declensionOfNumbers_5() {
        final var countValue = 5;
        assertEquals(getNameCount(countValue), "раз");
    }

    @Test
    void declensionOfNumbers_19() {
        final var countValue = 19;
        assertEquals(getNameCount(countValue), "раз");
    }

    @Test
    void declensionOfNumbers_22() {
        final var countValue = 22;
        assertEquals(getNameCount(countValue), "раза");
    }

    @Test
    void declensionOfNumbers_100() {
        final var countValue = 1;
        assertEquals(getNameCount(countValue), "раз");
    }

    private static String getNameCount(int countValue) {
        return MessagesUtils.declensionOfNumbers(countValue, "раз", "раза", "раз");
    }


}