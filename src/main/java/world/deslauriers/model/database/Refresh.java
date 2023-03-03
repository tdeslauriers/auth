package world.deslauriers.model.database;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.serde.annotation.Serdeable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Serdeable
public record Refresh(

        @Id @GeneratedValue Long id,
        @NonNull @NotBlank String username,
        @NonNull @NotBlank String refreshToken,
        @NonNull @NotNull Boolean revoked,
        @DateCreated @NonNull @NotNull Instant dateCreated

) {
    public Refresh(@NonNull String username, @NonNull String refreshToken, @NonNull Boolean revoked, @NonNull Instant dateCreated) {
        this(null, username, refreshToken, revoked, dateCreated);
    }
}
