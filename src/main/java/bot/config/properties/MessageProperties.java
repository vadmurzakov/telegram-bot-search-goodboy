package bot.config.properties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties
public class MessageProperties {
    private final Map<String, List<String>> messages = new HashMap<>();
}
