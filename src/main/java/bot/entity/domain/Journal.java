package bot.entity.domain;

import jakarta.persistence.Entity;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Journal extends AbstractPersistable<UUID> {
    private UUID userId;
    private Long chatId;
    private LocalDate createDate;

    public Journal(UUID userId, Long chatId) {
        this.userId = userId;
        this.chatId = chatId;
        this.createDate = LocalDate.now();
    }
}
