package bot.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "messages")
public class MessageProperties {
    private List<String> rooster;
    private List<String> goodBoy;
}
