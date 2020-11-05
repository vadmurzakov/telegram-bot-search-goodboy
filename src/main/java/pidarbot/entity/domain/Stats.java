package pidarbot.entity.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.GenerationType.SEQUENCE;

@Data
@Builder
@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
public class Stats {
    @Id
    @SequenceGenerator(name = "seq_stats_id", sequenceName = "seq_stats_id", allocationSize = 1)
    @GeneratedValue(strategy = SEQUENCE, generator = "seq_stats_id")
    private Long id;
    private Long chatId;
    private Long userId;
    private boolean active;
    private Long countPidrDay;
    private Long countGoodBoy;

    public Stats(Long chatId, Long userId) {
        this.chatId = chatId;
        this.userId = userId;
        this.active = true;
        this.countPidrDay = 0L;
        this.countGoodBoy = 0L;
    }
}
