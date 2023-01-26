package bot.entity.domain;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import liquibase.util.StringUtils;
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
public class User {
    @Id
    @GeneratedValue
    private UUID id;
    private Long userTelegramId;
    private String username;
    private String firstName;
    private String lastName;

    public String getFullName() {
        String fullName = this.firstName;
        if (StringUtils.isNotEmpty(this.getUsername())){
            fullName += " (@" + this.getUsername() + ")";
        }
        return fullName;
    }
}
