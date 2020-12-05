package pidarbot.entity.domain;

import liquibase.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.GenerationType.SEQUENCE;

/**
 * @author Murzakov Vadim <murzakov.vadim@otr.ru>
 */
@Data
@Builder
@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @SequenceGenerator(name = "seq_user_id", sequenceName = "seq_user_id", allocationSize = 1)
    @GeneratedValue(strategy = SEQUENCE, generator = "seq_user_id")
    private Long id;
    private Integer userTelegramId;
    private String username;
    private String firstName;
    private String lastName;

    public String getFullName() {
        String fullName = this.firstName + " " + this.lastName;
        if (StringUtils.isNotEmpty(this.getUsername())){
            fullName += " (@" + this.getUsername() + ")";
        }
        return fullName;
    }
}
