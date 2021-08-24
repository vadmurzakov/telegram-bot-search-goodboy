package pidarbot.service.business;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pidarbot.config.properties.MessageProperties;

import static pidarbot.util.RandomUtil.generateRandomNumber;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageProperties messageProperties;

    public String randomPidrMessage() {
        int index = generateRandomNumber(messageProperties.getPidr().size() - 1);
        return messageProperties.getPidr().get(index);
    }

    public String randomGoodBoyMessage() {
        int index = generateRandomNumber(messageProperties.getGoodBoy().size() - 1);
        return messageProperties.getGoodBoy().get(index);
    }

}
