package world.deslauriers.model.database;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import io.micronaut.serde.annotation.Serdeable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Serdeable
@MappedEntity
public record PasswordHistory(
        @Id @GeneratedValue Long id,
        @NonNull @NotBlank @Size(max = 255) String password,
        @NonNull LocalDate updated,
        @Nullable @Relation(Relation.Kind.MANY_TO_ONE) User user
) {

    public PasswordHistory(Long id, @NonNull String password, @NonNull LocalDate updated) {
        this(id, password, updated, null);
    }

    public PasswordHistory(@NonNull String password, @NonNull LocalDate updated, User user) {
        this(null, password, updated, user);
    }
}
