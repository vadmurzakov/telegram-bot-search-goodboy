package bot.entity.domain;

import java.time.LocalDate;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
public class Stats {
    @Id
    @GeneratedValue
    private UUID id;
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
