package pidarbot.config.properties;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Data
@Component
public class MessageProperties {
    private List<String> pidr;
    private List<String> goodBoy;

    /**
     * В проперти вынести не получается, ибо формат там ISO-8859-1
     */
    public MessageProperties() {
        this.pidr = Arrays.asList(
                "{0}, ебать сегодня тебя буду :*",
                "Пидарасина дня - {0}",
                "{0}, иди брей жопу",
                "{0} - пидарас, пидарасина",
                "Счастливый билет в раздолбанное очко получает {0}",
                "{0}, сегодня твое очко принадлежит мне"
        );

        this.goodBoy = Arrays.asList(
                "{0}, хороший мальчик",
                "Красавчик-Дня {0}"
        );
    }
}
