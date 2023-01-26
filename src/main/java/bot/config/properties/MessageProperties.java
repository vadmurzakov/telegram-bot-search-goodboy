package bot.config.properties;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "messages")
public class MessageProperties {
    private List<String> rooster;
    private List<String> alreadyStarted;
    private List<String> stats;
}
