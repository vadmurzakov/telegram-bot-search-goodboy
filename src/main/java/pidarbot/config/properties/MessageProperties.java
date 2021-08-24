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
                "Пушок-дня: {0}",
                "{0}, иди брей жопу",
                "{0} - пидарас, пидарасина",
                "засунул палец в попку {0}",
                "{0} - haha, pidor blya",
                "{0}, сегодня твоё очко моё"
        );

        this.goodBoy = Arrays.asList(
                "{0}, хороший мальчик",
                "Красавчик-Дня {0}",
                "{0}, ты себя хорошо сегодня вел",
                "{0} - сын маминой подруги"
        );
    }
}
