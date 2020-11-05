package pidarbot.service.providers.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PidorProvidersTest {

    @Test
    public void generateRandomNumber() {
        int min = 0;
        int max = 5;
        for (int i = 0; i < 1000; i++) {
            int random = min + (int) (Math.random() * max);
            assertTrue(random >= min);
            assertTrue(random <= max);
        }
    }

}