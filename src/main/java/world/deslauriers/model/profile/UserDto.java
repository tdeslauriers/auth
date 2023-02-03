package world.deslauriers.model.profile;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Serdeable
public record UserDto(
        @NonNull Long id,
        @NonNull @NotBlank @Email @Size(max = 255) String username,
        @NonNull @NotBlank @Size(min = 1, max = 32) String firstname,
        @NonNull @NotBlank @Size(min = 1, max = 32) String lastname,
        @JsonFormat(pattern="yyyy-MM-dd") @NonNull LocalDate dateCreated,
        @NonNull Boolean enabled,
        @NonNull Boolean accountExpired,
        @NonNull Boolean accountLocked,
        @JsonFormat(pattern="yyyy-MM-dd") @Nullable LocalDate birthday,
        @Nullable String uuid
) {}
