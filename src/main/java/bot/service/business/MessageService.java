package bot.service.business;

import static bot.util.RandomUtil.generateRandomNumber;

import bot.config.properties.MessageProperties;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageProperties messageProperties;

    public String randomRoosterMessage() {
        int index = generateRandomNumber(messageProperties.getRooster().size() - 1);
        return decode(messageProperties.getRooster().get(index));
    }

    public String randomAlreadyStartedMessage() {
        int index = generateRandomNumber(messageProperties.getAlreadyStarted().size() - 1);
        return decode(messageProperties.getAlreadyStarted().get(index));
    }

    public String randomStatsMessage() {
        int index = generateRandomNumber(messageProperties.getStats().size() - 1);
        return decode(messageProperties.getStats().get(index));
    }

    private String decode(String encode) {
        byte[] decode = Base64.getDecoder().decode(encode);
        return new String(decode);
    }

}
