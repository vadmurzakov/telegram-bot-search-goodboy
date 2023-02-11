package bot.entity.domain;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Generated
@MappedSuperclass
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable<PK extends Serializable> extends AbstractPersistable<PK> {
    @CreatedDate
    private @NotNull LocalDateTime createdDate;

    @LastModifiedDate
    private @NotNull LocalDateTime lastModifiedDate;
}
