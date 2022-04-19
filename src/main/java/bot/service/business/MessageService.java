package bot.service.business;

import bot.config.properties.MessageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Base64;

import static bot.util.RandomUtil.generateRandomNumber;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageProperties messageProperties;

    public String randomRoosterMessage() {
        int index = generateRandomNumber(messageProperties.getRooster().size() - 1);
        return decode(messageProperties.getRooster().get(index));
    }

    public String randomGoodBoyMessage() {
        int index = generateRandomNumber(messageProperties.getGoodBoy().size() - 1);
        return decode(messageProperties.getGoodBoy().get(index));
    }

    private String decode(String encode) {
        byte[] decode = Base64.getDecoder().decode(encode);
        return new String(decode);
    }

}
