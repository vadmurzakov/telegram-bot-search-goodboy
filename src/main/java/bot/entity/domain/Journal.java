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
public class Journal {
    @Id
    @GeneratedValue
    private UUID id;
    private Long userTelegramId;
    private LocalDate createDate;

    public Journal(Long userTelegramId) {
        this.userTelegramId = userTelegramId;
        this.createDate = LocalDate.now();
    }
}
