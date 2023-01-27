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
    private Long userTelegramId;
    private LocalDate createDate;

    public Journal(Long userTelegramId) {
        this.userTelegramId = userTelegramId;
        this.createDate = LocalDate.now();
    }
}
