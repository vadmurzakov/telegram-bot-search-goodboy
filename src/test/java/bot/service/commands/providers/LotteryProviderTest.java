package bot.service.commands.providers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.Period;
import org.junit.jupiter.api.Test;

class LotteryProviderTest {

    @Test
    void betweenDate() {
        LocalDate aDate = LocalDate.of(2023, 1, 29);
        LocalDate bDate = LocalDate.of(2023, 2, 11);

        Period period = Period.between(aDate, bDate);
        int diff = Math.abs(period.getDays());

        assertEquals(13, diff);
    }
}