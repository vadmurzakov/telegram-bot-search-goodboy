package bot.config.properties;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "telegram")
@ToString(exclude = {"token", "adminId"})
public class TelegramProperties {
    private String token;
    private String username;
    private Long adminId;
}
