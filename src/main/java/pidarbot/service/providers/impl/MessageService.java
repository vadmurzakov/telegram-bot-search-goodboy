package pidarbot.service.providers.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pidarbot.config.properties.MessageProperties;

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

    private int generateRandomNumber(int max) {
        return 1 + (int) (Math.random() * max);
    }

}
