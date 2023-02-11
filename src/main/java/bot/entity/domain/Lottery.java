package bot.entity.domain;

import jakarta.persistence.Entity;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Lottery extends Auditable<UUID> {
    /**
     * Суррогатный ID пользователя из БД.
     */
    private UUID userId;
    /**
     * Идентификатор чата в котором запущена ставка.
     */
    private Long chatId;
    /**
     * Последний день когда запускали.
     */
    private LocalDate lastDayRun;
    /**
     * Идентификатор сообщений запуска
     * нужен чтобы отслеживать callback ставки.
     */
    private Integer lastMessageId;
    /**
     * Номера выигрышных билетов.
     */
    private Set<Integer> tickets;
}

