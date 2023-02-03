package bot.entity.domain;

import jakarta.persistence.Entity;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class User extends AbstractPersistable<UUID> {
    @NotNull
    private Long userTelegramId;
    @Nullable
    private String username;
    @NotNull
    private String firstName;
    @Nullable
    private String lastName;

    @NotNull
    @Override
    public String toString() {
        var fullName = firstName;
        if (StringUtils.isNotEmpty(username)) {
            fullName += " (@" + username + ")";
        }
        return fullName;
    }
}
