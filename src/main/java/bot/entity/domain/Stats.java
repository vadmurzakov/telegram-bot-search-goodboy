package bot.entity.domain;

import jakarta.persistence.Entity;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Stats extends AbstractPersistable<UUID> {
    private UUID userId;
    private Long chatId;
    private boolean active;
    private Long countRooster;
    private LocalDate lastDayRooster;
    private Integer lastMessageId;

    public Stats(Long chatId, UUID userId) {
        this.chatId = chatId;
        this.userId = userId;
        this.active = true;
        this.countRooster = 0L;
    }
}
