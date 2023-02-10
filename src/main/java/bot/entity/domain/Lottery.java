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
public class Lottery extends AbstractPersistable<UUID> {
    /**
     * Суррогатный ID пользователя из БД.
     */
    private UUID userId;
    /**
     * Идентификатор чата в котором запущена ставка
     */
    private Long chatId;
    /**
     * Последний день когда запускали
     */
    private LocalDate lastDayRun;
    /**
     * Идентификатор сообщений запуска
     * нужен чтобы отслеживать ставку.
     */
    private Integer lastMessageId;

    public Lottery(Long chatId, UUID userId, Integer lastMessageId) {
        this.chatId = chatId;
        this.userId = userId;
        this.lastMessageId = lastMessageId;
    }
}
