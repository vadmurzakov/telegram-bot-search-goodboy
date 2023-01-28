package bot.service.business;

import static bot.entity.enums.MessageTemplateEnum.REGISTRATION;
import static bot.util.RandomUtil.generateRandomNumber;

import bot.config.properties.MessageProperties;
import bot.entity.enums.MessageTemplateEnum;
import java.util.Base64;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageProperties messageProperties;

    /**
     * Получить рандомный шаблон сообщения.
     *
     * @param template тип шаблона сообщений.
     * @return шаблон сообщения в человеко-читаемом виде.
     */
    public String randomMessage(final MessageTemplateEnum template) {
        final var messages = messageProperties.getMessages().get(template.name().toLowerCase(Locale.ROOT));
        int randomIndex = generateRandomNumber(messages.size());
        return template == REGISTRATION ? messages.get(randomIndex) : decode(messages.get(randomIndex));
    }

    /**
     * Декодирует Base64 в человеко-читаемый формат. Т.к. все шаблоны хранятся в base64
     *
     * @param encode закодированный шаблон в формате Base64.
     * @return шаблон сообщения в человеческом виде.
     */
    private String decode(String encode) {
        byte[] decode = Base64.getDecoder().decode(encode);
        return new String(decode);
    }

}
